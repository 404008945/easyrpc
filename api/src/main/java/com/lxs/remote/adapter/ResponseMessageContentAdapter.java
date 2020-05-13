package com.lxs.remote.adapter;

import com.lxs.remote.MessageContent;

import java.io.Serializable;

public abstract class ResponseMessageContentAdapter<T> implements MessageContent<Object,T>, Serializable {

    protected String key;

    @Override
    public Object getUrl() {
        return null;
    }

    @Override
    public Object[] getArgs() {
        return null;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
