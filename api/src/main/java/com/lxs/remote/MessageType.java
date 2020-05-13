package com.lxs.remote;

public enum MessageType {
    HEART_TYPE("心跳包",(byte)0),
    MESSAGE_TYPE("内容包",(byte)1),
    REGIST_TYPE("注册服务包",(byte)2);

    private String name;
    private byte index;


    private MessageType(String name, byte index) {
        this.name = name;
        this.index = index;
    }

    public static MessageType valueOf(byte b){
        switch (b)
        {
            case 0:return HEART_TYPE;
            case 1:return MESSAGE_TYPE;
            case 2:return REGIST_TYPE;
        }
        return null;
    }

    public byte getValue(){
        return index;
    }
}
