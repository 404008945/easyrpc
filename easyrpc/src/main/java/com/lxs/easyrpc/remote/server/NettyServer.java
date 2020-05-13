package com.lxs.easyrpc.remote.server;

import com.lxs.easyrpc.config.EasyRpcProperties;
import com.lxs.easyrpc.config.SpringUtil;
import com.lxs.exception.RpcException;
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

    public NettyServer(){
        if(SpringUtil.getBean(EasyRpcProperties.class).getPort()!=0) {
            start();
        }
    }

    public void start()  {
        try {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline()
                                .addLast("decoder", new NettyDecoder())   // 1
                                .addLast("encoder", new NettyEncode())  // 2
//                                .addLast("aggregator", new HttpObjectAggregator(256 * 1024))    // 3
                                .addLast("handler", new NettyServerHandler());        // 4
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        b.bind(SpringUtil.getBean(EasyRpcProperties.class).getPort()).sync();

        }catch (Exception e)
        {
            log.error("服务启动失败:{}",e);
            throw  new RpcException("服务启动失败",e);
        }
    }
}