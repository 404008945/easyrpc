package com.lxs.remote;

import com.lxs.remote.adapter.RequestMessageContentAdapter;

/**
 * 客户端请求注册中心，只需要一个url，其中url即可
 */
public class RegisterRequestMessageContent<String> extends RequestMessageContentAdapter<String> {

    public RegisterRequestMessageContent(String url){
        this.url=url;
    }

    private String url;


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {//targetInterface
        return this.url;
    }

    @Override
    public Object[] getArgs() {
        return null;
    }



}
