package com.lxs.easyrpc.remote;

public class Remote {

    private String ipAddress;


    private Integer port;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Remote(String ipAddress, Integer port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj instanceof Remote) {
            Remote r =(Remote)obj;
           return this.ipAddress.equals(r.ipAddress)&&this.port.equals(r.port);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
