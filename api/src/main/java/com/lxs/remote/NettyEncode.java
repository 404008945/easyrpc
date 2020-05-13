package com.lxs.remote;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class NettyEncode extends MessageToByteEncoder<Message> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {

        Thread thread  = Thread.currentThread();
        byteBuf.writeByte(message.getType().getValue());
        if(message.getPort()==null)
        {
            message.setPort(0);
        }
        byteBuf.writeInt(message.getPort());
        if(message.getContent()!=null)
        {
        byte []b = null;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(message.getContent());
        b = bo.toByteArray();
        byteBuf.writeInt(b.length);
        byteBuf.writeBytes(b);
        }else{
            byteBuf.writeInt(0);
        }

    }
}