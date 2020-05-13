package com.lxs.remote.adapter;

import com.lxs.remote.MessageContent;

import java.io.Serializable;

public abstract class RequestMessageContentAdapter<T> implements MessageContent<T,Object>, Serializable {
    protected String key;

    @Override
    public Object getResult() {
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
