package com.lxs.remote;

public interface MessageContent<T1, T2> {
    String getKey();//一起请求的唯一标识，要求请求发送

    T1 getUrl();

    Object[] getArgs();

    T2 getResult();
}
