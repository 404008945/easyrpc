package com.lxs.easyrpc.proxy.exporter;

import com.lxs.easyrpc.proxy.Invoke;
import com.lxs.easyrpc.proxy.Result;
import com.lxs.easyrpc.proxy.url.URL;

import java.lang.reflect.Method;


/**
 * 注册服务，注册到注册中心
 */
public class Exporter {
    //提供暴露服务，
    private Invoke invoke;

    //采用一个erporter一个方法的形式
    private Method method;

    private URL url;//作为key

    public Exporter(Invoke invoke, Method method) {
        this.invoke = invoke;
        this.method = method;
        this.url = new URL(method);
    }



    //调用服务调用者，需要给出入参
    /**
     * 调用服务，做一个转换将url转换成调用
     * @return
     */
    public Result invoke(Object args[]){
        return  invoke.invoke(method,args);
    }

}
