package com.lxs.easyrpc.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//类级注解 生产者接口注册
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcProd {
    /*
    默认超时时间为5s
     */
    public int timeout() default 5;//

}
