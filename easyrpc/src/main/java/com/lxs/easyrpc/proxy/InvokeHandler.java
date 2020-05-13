package com.lxs.easyrpc.proxy;

import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
@Slf4j
public class InvokeHandler implements InvocationHandler {

    private Invoke invoke;

    private int retryTime =3;//默认为3

    public InvokeHandler(Invoke invoke,int retryTime) {
        this.invoke = invoke;
        this.retryTime=retryTime;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoke.toString();
        }
        Result result = null;
        for(int i =0;i<retryTime;i++) {
            try {
                result = (Result) invoke.invoke(method, args);
            } catch (RpcException e)//如果是
            {
                if (e.getCode() == RpcException.TIMEOUT_EXCEPTION) {
                    //将会重试
                    log.error("服务调用超时:{}",method);
                    if(i==retryTime-1)//重试次数用尽
                    {
                        throw new RpcException(RpcException.TIMEOUT_EXCEPTION,"服务调用超时"+ URL.transferUrl(method));
                    }
                    continue;
                }else {
                    throw e;
                }
            }
            break;
        }
        if(result.hasException())
        {
             throw result.getException();
        }
        return result.getResult();
    }
}
