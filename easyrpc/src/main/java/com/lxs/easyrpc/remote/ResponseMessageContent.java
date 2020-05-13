package com.lxs.easyrpc.remote;

import com.lxs.easyrpc.proxy.Result;
import com.lxs.easyrpc.proxy.url.URL;
import com.lxs.remote.adapter.ResponseMessageContentAdapter;



public class ResponseMessageContent extends ResponseMessageContentAdapter<Result> {

    private Result result;

    public URL getUrl() {
        return null;
    }

    @Override
    public Object[] getArgs() {
        return null;
    }



    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public  ResponseMessageContent(String key, Result result) {
       this.key = key;
        this.result = result;
    }
}
