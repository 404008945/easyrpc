package com.lxs.easyrpc.remote;

import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;


public class LiveChannelCache {

    private Channel channel;
    private ScheduledFuture scheduledFuture;  //用来关闭没有心跳的连接
     public   LiveChannelCache(){

    }

    public LiveChannelCache(Channel channel) {
        this.channel = channel;
    }

    public LiveChannelCache(Channel channel, ScheduledFuture scheduledFuture) {
        this.channel = channel;
        this.scheduledFuture = scheduledFuture;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ScheduledFuture getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}
