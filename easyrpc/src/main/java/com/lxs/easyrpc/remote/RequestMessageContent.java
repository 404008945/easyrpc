package com.lxs.easyrpc.remote;

import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.remote.adapter.RequestMessageContentAdapter;


public class RequestMessageContent  extends RequestMessageContentAdapter<URL>   {

    private URL url;

    private Object[] args;//接口入参

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public RequestMessageContent(URL url, Object[] args) {
        this.url = url;
        this.args = args;
    }
}
