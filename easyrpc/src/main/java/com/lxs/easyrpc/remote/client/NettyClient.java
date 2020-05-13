package com.lxs.easyrpc.remote.client;

import com.lxs.easyrpc.config.EasyRpcProperties;
import com.lxs.easyrpc.config.SpringUtil;
import com.lxs.exception.RpcException;
import com.lxs.easyrpc.remote.*;
import com.lxs.remote.Message;
import com.lxs.remote.MessageType;
import com.lxs.remote.NettyDecoder;
import com.lxs.remote.NettyEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用来发送消息，注册服务到注册中心，消费者调用生产者请求，发送者生命周期比较短
 */
@Slf4j
public class NettyClient {

    private EventLoopGroup group;

    private  Bootstrap b;

    //只需要简单的put和remove原子操作 线程安全集合完全满足条件
    private Map<Remote, LiveChannelCache> remoteMap = new ConcurrentHashMap<>();
    private static final DefaultEventLoop NETTY_RESPONSE_PROMISE_NOTIFY_EVENT_LOOP = new DefaultEventLoop(null, new NamedThreadFactory("NettyResponsePromiseNotify"));

    public NettyClient(){
        init();
    }
    public void startHeartBeat(){//心跳包只给zk发送,心跳包时间间隔可配置
        //单独启动一个线程，启动后，不停进行心跳发送
        new Thread(()->{
            while (true)//考虑消息格式
            {
                try {

                    Thread.sleep(Long.valueOf(SpringUtil.getBean(EasyRpcProperties.class).getHeartBeatTime()*1000));
                    //发送心跳包,心跳包固定长度6
                    Message message = new Message(0, MessageType.HEART_TYPE);
                    message.setPort(SpringUtil.getBean(EasyRpcProperties.class).getPort());
                    //组装注册中心地址
                    sendMessage(new Remote(SpringUtil.getBean(EasyRpcProperties.class).getRegisterIp(),SpringUtil.getBean(EasyRpcProperties.class).getRegisterport()),message);

                } catch (Exception e) {
                   log.error("心跳发送失败:{}",e);
                }
            }
        }).start();
    }

    private void init(){
        group = new NioEventLoopGroup();
         b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p//.addLast("handler",new NettyClientHandler())
                                .addLast("encoder", new NettyEncode())
                                .addLast("decoder", new NettyDecoder())
                                .addLast("handler",new NettyClientHandler())
                               ;
                    }
                });
        if(SpringUtil.getBean(EasyRpcProperties.class).getPort()!=0){//配置端口号才启动服务器
          startHeartBeat();
        }
//        Remote remote = new Remote("127.0.0.1",20885);
//        URL url  = new URL(UserService.class.getMethods()[0]);
//        Object []args = new Object[1];
//        args[0] = "world";
//        Message message = new Message(MessageType.MESSAGE_TYPE,new RequestMessageContent(url,args));

       // sendMessage(remote,message);
    }

    /**
     * 需要返回值的调用,使用countdownLatch控制
     * @param remote
     * @param message
     * @return
     */
    @SneakyThrows
    public Object sendMessageAndReturn(Remote remote, Message message,int timeout)
    {
        Promise<Object> defaultPromise = NETTY_RESPONSE_PROMISE_NOTIFY_EVENT_LOOP.newPromise();
        ChannelFuture future = null;
        if (!remoteMap.containsKey(remote)||!remoteMap.get(remote).getChannel().isOpen()||!remoteMap.get(remote).getChannel().isActive())//不包含就加入
        {
            future = b.connect(remote.getIpAddress(), remote.getPort()).sync();

            log.info("建立连接:{}",remoteMap.containsKey(remote));
            remoteMap.put(remote,new LiveChannelCache(future.channel()));
        }


        Channel channel  = remoteMap.get(remote).getChannel();

        AttributeKey<RequestContext> CURRENT_REQ_BOUND_WITH_THE_CHANNEL =
                AttributeKey.valueOf(message.getContent().getKey());
        RequestContext context = new RequestContext(message.getContent(),defaultPromise);
        channel.attr(CURRENT_REQ_BOUND_WITH_THE_CHANNEL).set(context);

        channel.writeAndFlush(message);
        /**
         * 客户端发key，服务端返回一个一样的key，key用来保证一个channel可能同时发多个消息消息不错乱
         */

//        future.addListener(new GenericFutureListener<Future<? super Void>>() {//通过监听设置
//            @Override
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                System.out.println(Thread.currentThread().getName() + " 请求发送完成");
//            }
//        });
        return  get(defaultPromise,timeout);
    }

    public <V> V get(Promise<V> future,int timeout) {
        // 1.
        if (!future.isDone()) {
            CountDownLatch l = new CountDownLatch(1);
            future.addListener(new GenericFutureListener<Future<? super V>>() {
                @Override
                public void operationComplete(Future<? super V> future) throws Exception {//利用回调解除等待
                    log.info("received response,listener is invoked");
                    if (future.isDone()) {
                        // 2
                        // promise的线程池，会回调该listener
                        l.countDown();
                    }
                }
            });
            //失败重试，默认三次

            boolean interrupted = false;
            if (!future.isDone()) {
                try {
                    // 3
                    l.await(timeout, TimeUnit.SECONDS);//接口超时时间
                } catch (InterruptedException e) {
                    log.error("e:{}", e);
                    interrupted = true;
                }

            }

            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        //4
        if (future.isSuccess()) {
            return future.getNow();
        }
        log.error("wait result time out. will be retry");
        throw new RpcException(RpcException.TIMEOUT_EXCEPTION,"服务调用超时");
    }
    public void  sendMessage(Remote remote, Message message){
        try {
            ChannelFuture future = null;
            if (!remoteMap.containsKey(remote)||!remoteMap.get(remote).getChannel().isOpen()||!remoteMap.get(remote).getChannel().isActive())//不包含就加入
            {
                 future = b.connect(remote.getIpAddress(), remote.getPort()).sync();

                 log.info("建立连接:{}",remoteMap.containsKey(remote));
                 remoteMap.put(remote,new LiveChannelCache(future.channel()));
            }

            Channel channel  = remoteMap.get(remote).getChannel();

            channel.writeAndFlush(message);
           // channel.closeFuture().sync();
        }catch (Exception e){
           //连接失败
            log.error("连接远程服务器失败:{}",e);
            throw new RpcException("连接远程服务器失败",e);

        }  finally{
            group.spliterator();
        }
    }


}
