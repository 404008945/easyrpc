package com.lxs.easyregister.remote;


import com.lxs.remote.NettyDecoder;
import com.lxs.remote.NettyEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 用来响应客户端请求的服务，调用invoke，返回结果
 */
@Slf4j
public class NettyServer {
    private int port=20888;
    private int heartBeat=30;

    public NettyServer(){
        start();
        log.info("注册中心已启动:{}",port);
    }
    public NettyServer(int port,int heartBeat){
        this.port=port;
        this.heartBeat=heartBeat;
        start();
        log.info("注册中心已启动:{}",port);
    }

    public void start()  {
        try {
            ServerBootstrap b = new ServerBootstrap();

            NioEventLoopGroup boos = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup();

        b.group(boos,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline()
                                .addLast("decoder", new NettyDecoder())   // 1
                                .addLast("encoder", new NettyEncode())  // 2
                                .addLast("handler", new NettyServerHandler(heartBeat));        // 3
                    }
                }).

        bind(port).sync();

        }catch (Exception e)
        {
            log.error("服务启动失败:{}",e);
          //  throw  new Exception("服务启动失败",e);
        }
    }
}