package com.lxs.easyrpc.proxy;

import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.exception.RpcException;

import java.lang.reflect.Method;


public interface Invoke<T> {

    /**
     * 用来欺骗调用者，好像真的调用时自己的实例
     *
     * @return service interface.
     */
    Class<T> getInterface();


    /**
     * 调用方法
     *
     */
    Result invoke(Method method, Object... args) throws RpcException;



    URL getUrl();//调用和传递数据


}
