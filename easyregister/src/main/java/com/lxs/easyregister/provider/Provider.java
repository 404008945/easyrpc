package com.lxs.easyregister.provider;

import io.netty.channel.Channel;

import java.util.concurrent.ScheduledFuture;

/**
 * 注册服务存储方式，包含url，作为key，channel用来通信用监视心跳（考虑如何做负载均衡）
 */
public class Provider {
    private String ipAddr;

    private int port;


    private boolean isActive; //是否提供服务

    private String  targetInterface;//目标接口

    private Channel channel;

    private ScheduledFuture scheduledFuture;  //用来关闭没有心跳的连接
    public   Provider(){

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
        this.getScheduledFuture().cancel(true);
        this.isActive = true;
        this.scheduledFuture = scheduledFuture;
    }


    @Override
    public int hashCode() {
        return 1;//写死，避免影响判重
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getHost(){
        return ipAddr+":"+port;
    }


    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }



    public String getTargetInterface() {
        return targetInterface;
    }

    public void setTargetInterface(String targetInterface) {
        this.targetInterface = targetInterface;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null)
        {
            return false;
        }
        if(obj instanceof Provider) {
            Provider provider = (Provider)obj;
            return this.ipAddr.equals(provider.ipAddr)&&this.targetInterface.equals(provider.targetInterface);
        }
        return false;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Provider(String ipAddr, int port,String targetInterface, Channel channel, ScheduledFuture scheduledFuture) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.targetInterface = targetInterface;
        this.channel = channel;
        this.scheduledFuture = scheduledFuture;
        this.isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
