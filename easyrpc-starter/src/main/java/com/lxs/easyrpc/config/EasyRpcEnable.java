package com.lxs.easyrpc.config;

import com.lxs.easyrpc.remote.client.NettyClient;
import com.lxs.easyrpc.remote.server.NettyServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableConfigurationProperties({EasyRpcProperties.class})
@Import({ SpringUtil.class,EasyRpcComponentScanRegister.class, NettyServer.class, NettyClient.class})

public @interface EasyRpcEnable {



}
