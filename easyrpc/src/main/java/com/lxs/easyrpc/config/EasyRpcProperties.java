package com.lxs.easyrpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easyrpc")
public class EasyRpcProperties {

    private String basePackage;

    private int port; //服务提供者默认占用端口

    private int heartBeatTime=15;//提供者多少秒发送一个心跳包

    private String registerIp;

    private int registerPort;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(int heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public int getRegisterport() {
        return registerPort;
    }

    public void setRegisterport(int registerport) {
        this.registerPort = registerport;
    }
}
