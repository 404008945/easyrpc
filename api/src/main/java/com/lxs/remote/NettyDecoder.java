package com.lxs.remote;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class NettyDecoder extends ReplayingDecoder<NettyDecoder.LiveState> {


    public enum LiveState {
        TYPE,
        LENGTH,
        CONTENT
    }

    private Message message;

    public NettyDecoder() {
        super(LiveState.TYPE);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        LiveState state = state();

        switch (state) {
            case TYPE:
                message = new Message();
                byte type = byteBuf.readByte();
                int port=byteBuf.readInt();
                message.setType(MessageType.valueOf(type));
                message.setPort(port);
                checkpoint(LiveState.LENGTH);//下一次接受长度数据
                break;
            case LENGTH:
                int length = byteBuf.readInt();
                message.setLength(length);
                if (length > 0) {
                    checkpoint(LiveState.CONTENT);
                } else {
                    list.add(message);
                    checkpoint(LiveState.TYPE);
                }
                break;
            case CONTENT://byte转对象
                byte[] bytes = new byte[message.getLength()];
                byteBuf.readBytes(bytes);
                ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
                ObjectInputStream oi = new ObjectInputStream(bi);

                MessageContent messageContent = (MessageContent) oi.readObject();

                message.setContent(messageContent);
                list.add(message);
                checkpoint(LiveState.TYPE);
                break;
            default:
                throw new IllegalStateException("invalid state:" + state);
        }
    }
}
