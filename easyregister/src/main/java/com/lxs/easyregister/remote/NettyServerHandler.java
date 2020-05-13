package com.lxs.easyregister.remote;

import com.lxs.easyregister.manager.ManagerFactory;
import com.lxs.easyregister.provider.Provider;
import com.lxs.remote.Message;
import com.lxs.remote.MessageType;
import com.lxs.remote.RegisterResponseMessageContent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
    public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private int heartBeat = 30;//默认30秒无心跳关闭连接

    public NettyServerHandler(int heartBeat){
        this.heartBeat=heartBeat;
    }
    /**
     * 读取消息并调用服务发送结果
     * @param ctx
     * @param
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object mg) throws Exception {
        log.info("接受到信息:{}",mg);
        Channel channel = ctx.channel();
        InetSocketAddress host = (InetSocketAddress)ctx.channel().remoteAddress();
        Message message = (Message)mg;
        String str="";
        if(message.getPort()!=0) {
            str=host.getAddress().getHostAddress() + ":" + message.getPort();
        }
        final  String ip = str;
        if(message.getType().equals(MessageType.REGIST_TYPE)){//心跳包，更新心跳
                ScheduledFuture scheduledFuture = ctx.executor().schedule(
                        () -> {
                            channel.close();
                        }, heartBeat, TimeUnit.SECONDS);//15秒收不到心跳包则关闭连接,认为不再能够提供服务
                //设置心跳超时
                channel.closeFuture().addListener(feature->{
                    ManagerFactory.getInstance().remove(ip);
                });
                Provider provider = new Provider(host.getAddress().getHostAddress(),message.getPort(),message.getContent().getUrl().toString(),channel,scheduledFuture);
                ManagerFactory.getInstance().registe(provider);


        }else if(message.getType().equals(MessageType.MESSAGE_TYPE))
        {
            //消费者请求消费，返回结果
            RegisterResponseMessageContent messageContent =ManagerFactory.getInstance().getProvider(message.getContent().getUrl().toString());
            messageContent.setKey(message.getContent().getKey());
            Message res = new Message(MessageType.MESSAGE_TYPE,messageContent);
            ctx.writeAndFlush(res);
        }else if(message.getType().equals(MessageType.HEART_TYPE)){
            ScheduledFuture scheduledFuture = ctx.executor().schedule(
                    () -> {
                        channel.close();
                    }, heartBeat, TimeUnit.SECONDS);//15秒收不到心跳包则关闭连接,认为不再能够提供服务

            //Provider provider = new Provider(ip,message.getContent().getUrl().toString(),channel,scheduledFuture);
            ManagerFactory.getInstance().updateHeartBeat(ip,scheduledFuture);
        }
        //心跳包或者消息体包

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      log.debug("channelReadComplete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("exceptionCaught");
        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
}
