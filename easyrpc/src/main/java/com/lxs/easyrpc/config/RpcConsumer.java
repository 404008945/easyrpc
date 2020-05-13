package com.lxs.easyrpc.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcConsumer {
    /*
    默认超时时间为5s
     */
    public int timeout() default 5;

    /*
    超时重试次数
     */
    public int retryTime() default 3;
}
