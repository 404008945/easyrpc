package com.lxs.easyrpc.proxy.impl;

import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.exception.RpcException;
import com.lxs.easyrpc.proxy.Invoke;
import com.lxs.easyrpc.proxy.Result;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 生产者invoke在注册erporter时生成url
 * @param <T>
 */
@Slf4j
public class ProdInvoke<T>  implements Invoke<T>, Serializable {

    private  Class<T> type;//接口类型

    private T ref; //引用实例,的实例如何调用方法

    private Method[] methods;


    private int timeout;//生产者超时时间

    public ProdInvoke(Class<T> type, T ref, Method[] methods,  int timeout) {
        this.type = type;
        this.ref = ref;
        this.methods = methods;
        this.timeout = timeout;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Method method ,Object ... args) throws RpcException {
        Result result = new InvokeResult();
        try {
    //获取执行方法，以及入参，第0个为方法路径，后面的为方法参数，如找不到此函数，抛出异常
            boolean has = false;
            for (Method m : methods) {
                if (m.equals(method)) {
                    has = true;
                    result.setResult(method.invoke(ref, args));
                }
            }
            if(!has)
            {
                throw  new RpcException(method+"方法不存在");
            }

        }catch (Exception e){
            result.setException(new RpcException("服务调用失败:"+e.getMessage()));//只抛rpcExceprion
            e.printStackTrace();
            log.error("调用异常:{}",e);
            return result;
        }

        return result;
    }

    @Override
    public URL getUrl() {
        return null;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T red) {
        this.ref = red;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
