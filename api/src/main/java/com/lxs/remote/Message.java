package com.lxs.remote;

public class Message {

    private int length;


    private MessageType type;

    private Integer port;//服务提供者端口号

    private MessageContent content;

    public   Message(){

    }

    public Message(MessageType type, MessageContent content) {
        this.type = type;
        this.content = content;
    }

    public Message(int length, MessageType type) {
        this.length = length;
        this.type = type;
    }


    public Message(int length, MessageType type, MessageContent content) {
        this.length = length;
        this.type = type;
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Message{" +
                "length=" + length +
                ", type=" + type +
                ", content=" + content +
                '}';
    }
}
