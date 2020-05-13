package com.lxs.easyrpc.proxy.impl;

import com.lxs.easyrpc.config.EasyRpcProperties;
import com.lxs.easyrpc.config.SpringUtil;
import com.lxs.easyrpc.proxy.Invoke;
import com.lxs.easyrpc.proxy.Result;
import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.easyrpc.remote.Remote;
import com.lxs.easyrpc.remote.RequestMessageContent;
import com.lxs.easyrpc.remote.ResponseMessageContent;
import com.lxs.easyrpc.remote.client.NettyClient;
import com.lxs.exception.RpcException;
import com.lxs.remote.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;
@Slf4j
public class ConsumerInvoke<T>  implements Invoke<T>, Serializable {

    private  Class<T> type;//接口类型

   public ConsumerInvoke(Class type){
       this.type = type;
    }
    private int timeout;//设置接口超时时间

    private Remote remote; //注册中心

   private  int port; //服务提供者port


    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Method method, Object... args) throws RpcException {//这里需要发送数据请求服务端
       //先从注册中心获取，服务端的ip地址，在调用服务提供者服务
        MessageContent  messageContent = new RegisterRequestMessageContent(URL.transferUrl(method));
        ((RegisterRequestMessageContent) messageContent).setKey(UUID.randomUUID().toString());
        remote = new Remote(SpringUtil.getBean(EasyRpcProperties.class).getRegisterIp(),SpringUtil.getBean(EasyRpcProperties.class).getRegisterport());//注册中心取提供者的地址
        Message message = new Message(MessageType.MESSAGE_TYPE,messageContent);
        RegisterResponseMessageContent registerMs=   (RegisterResponseMessageContent) ((Message)SpringUtil.getBean(NettyClient.class).sendMessageAndReturn(remote,message,timeout)).getContent();
        if(registerMs.getResult().getMessageType()==registerMs.getResult().FAIL)
        {
            log.error("调用服务出错:{},{}", URL.transferUrl(method),registerMs.getResult().getErrCause());
            throw new RpcException(registerMs.getResult().getErrCause());
        }
        RequestMessageContent requestMessageContent = new RequestMessageContent(new URL(method),args);
        requestMessageContent.setKey(UUID.randomUUID().toString());
        Message message1 = new Message(MessageType.MESSAGE_TYPE,requestMessageContent);
        Remote re = new Remote(registerMs.getResult().getIpAddr(),registerMs.getResult().getPort());

        ResponseMessageContent resMessageContent =
                (ResponseMessageContent)((Message)SpringUtil.getBean(NettyClient.class).sendMessageAndReturn(re,message1,timeout)).getContent();//提供者地址
     //   Result result =resMessageContent.getResult();
        return resMessageContent.getResult();
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ConsumerInvoke{" +
                "type=" + type +
                '}';
    }

    @Override
    public URL getUrl() {
        return null;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
