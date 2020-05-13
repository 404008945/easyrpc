package com.lxs.remote;

import java.io.Serializable;
import java.security.Provider;

public class RegisterResult implements Serializable {
    public  static final byte FAIL=0;

    public  static final byte SUCCESS=1;

    private byte messageType;

    private  String  targetInterface;

    private String   ipAddr;//需要请求的服务端

    private int port;


    private  String  errCause;//错误原因



    public String getTargetInterface() {
        return targetInterface;
    }

    public void setTargetInterface(String targetInterface) {
        this.targetInterface = targetInterface;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }



    public String getErrCause() {
        return errCause;
    }

    public void setErrCause(String errCause) {
        this.errCause = errCause;
    }

    public RegisterResult(byte  type, String errCause) {
        this.messageType = type;
        this.errCause = errCause;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public RegisterResult() {

    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }
}
