package com.lxs.easyregister;

        import com.lxs.easyregister.remote.NettyServer;
        import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EasyRegister {

    public static void main(String[] args) {
        if (args != null && args.length != 0) {
            new NettyServer(Integer.valueOf(args[0]), Integer.valueOf(args[1]));//启动服务器
        }else {
            new NettyServer();
        }
        // nettyServer.start();
    }
}
