package com.lxs.easyrpc.remote.client;

import com.lxs.easyrpc.remote.RequestMessageContent;
import com.lxs.remote.MessageContent;
import io.netty.util.concurrent.Promise;

public class RequestContext {

    private MessageContent requestMessageContent;

    private Promise<Object> promise;


    public MessageContent getRequestMessageContent() {
        return requestMessageContent;
    }

    public RequestContext(MessageContent requestMessageContent, Promise<Object> promise) {
        this.requestMessageContent = requestMessageContent;
        this.promise = promise;
    }

    public void setRequestMessageContent(MessageContent requestMessageContent) {
        this.requestMessageContent = requestMessageContent;
    }

    public Promise<Object> getPromise() {
        return promise;
    }

    public void setPromise(Promise<Object> promise) {
        this.promise = promise;
    }
}
