package com.lxs.easyrpc.proxy.url;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class URL implements Serializable {
    //需要将接口转换成路径加参数类型的形式
    private String url;//含接口，方法，参数,例如a.b.c(param)

    public URL(Method method)
    {
        this.url=transferUrl(method);
    }
    /**
     * 转换成url
     * @return
     */
    public  static String transferUrl(Method method){
        String str= "";
        str = str+method.getDeclaringClass()+"."+method.getName()+"("
        + Arrays.asList(method.getParameterTypes()).stream().map(s->s.getName()).collect(joining(",")) + ")";
        return str;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object url) {
        if(url instanceof URL) {
            return this.url.equals(((URL)url).getUrl());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
