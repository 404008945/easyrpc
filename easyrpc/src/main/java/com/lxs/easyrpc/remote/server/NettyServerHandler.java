package com.lxs.easyrpc.remote.server;

import com.lxs.easyrpc.proxy.Result;
import com.lxs.easyrpc.proxy.exporter.ExporterFacory;
import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.easyrpc.remote.ResponseMessageContent;
import com.lxs.remote.Message;
import com.lxs.remote.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
    public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取消息并调用服务发送结果
     * @param channelHandlerContext
     * @param
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object mg) throws Exception {
        Message message = (Message)mg;
        if(message.getType().equals(MessageType.MESSAGE_TYPE)) {
            Result result = ExporterFacory.getInstance().invoke((URL)message.getContent().getUrl(), message.getContent().getArgs());
            Message msg = new Message(MessageType.MESSAGE_TYPE, new ResponseMessageContent(message.getContent().getKey(),result));//发送有内容的消息，Length是不确定的
            channelHandlerContext.channel().writeAndFlush(msg);
        }
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
