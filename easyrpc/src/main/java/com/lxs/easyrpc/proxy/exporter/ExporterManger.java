package com.lxs.easyrpc.proxy.exporter;

import com.lxs.easyrpc.config.EasyRpcProperties;
import com.lxs.easyrpc.config.SpringUtil;
import com.lxs.easyrpc.proxy.Result;
import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.easyrpc.remote.Remote;
import com.lxs.easyrpc.remote.client.NettyClient;
import com.lxs.remote.Message;
import com.lxs.remote.MessageType;
import com.lxs.remote.RegisterRequestMessageContent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理露服务，用于收到服务调用时，需要使用线程池同时要注意线程安全
 */
@Slf4j
public class ExporterManger {

    //这里的url带host信息，只有接口信息
    private Map<URL,Exporter> exporterMap = new HashMap<>();//注册新服务，需要注意线程安全。

    /**
     * 通过url调用服务
     * @param url
     * @return
     */

    public Result invoke(URL url, Object args[]){
        //json处理成相应的对象传过去
        return exporterMap.get(url).invoke(args);
    }


    public void export(URL url,Exporter exporter){
        //同时将服务暴露出去
        try {
            RegisterRequestMessageContent messageContent = new RegisterRequestMessageContent(url.getUrl());//注册服务需要一个urL
            Message message = new Message(MessageType.REGIST_TYPE, messageContent);
            message.setPort(SpringUtil.getBean(EasyRpcProperties.class).getPort());
            Remote remote = new Remote(SpringUtil.getBean(EasyRpcProperties.class).getRegisterIp(), SpringUtil.getBean(EasyRpcProperties.class).getRegisterport());
            SpringUtil.getBean(NettyClient.class).sendMessage(remote, message);

            exporterMap.put(url, exporter);
        }catch (Exception e)
        {
            log.error(url+"注册服务失败:{}",e);
        }
    }
}
