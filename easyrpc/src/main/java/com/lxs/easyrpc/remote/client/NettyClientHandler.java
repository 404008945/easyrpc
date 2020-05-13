package com.lxs.easyrpc.remote.client;

import com.lxs.remote.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       log.info("Client01Handler Active");
      //  ctx.fireChannelActive();  // 若把这一句注释掉将无法将event传递给下一个ClientHandler
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        log.info("message:{}",message);
        log.info(message.getContent().getKey());
        if(message.getContent().getKey()!=null) {
            RequestContext requestContext = (RequestContext) channelHandlerContext.channel().attr(AttributeKey.valueOf(message.getContent().getKey())).get();
            //需要相应的线程进行唤醒
            requestContext.getPromise().setSuccess(message);//成功返回数据
         //   channelHandlerContext.channel().attr(AttributeKey.valueOf(message.getContent().getKey())).set(null);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       super.channelReadComplete(ctx);
    }
}
