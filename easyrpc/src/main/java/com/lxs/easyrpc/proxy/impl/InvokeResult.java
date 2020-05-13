package com.lxs.easyrpc.proxy.impl;

import com.lxs.easyrpc.proxy.Result;

import java.io.Serializable;

public class InvokeResult implements Result, Serializable {

    private Object result;

    private Exception exception;

    private boolean hasException;

    @Override
    public Object getResult() {
        return result;
    }

    public void setResult(Object result){
        this.result=result;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean hasException() {
        return hasException;
    }

    @Override
    public void setException(Exception e) {
        this.exception = exception;
        hasException = true;
    }
}

