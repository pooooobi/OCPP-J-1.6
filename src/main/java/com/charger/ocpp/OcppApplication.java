package com.charger.ocpp;

import com.charger.ocpp.netty.NettyWebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OcppApplication {

    public static ApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(OcppApplication.class, args);
        NettyWebSocketServer nettyWebSocketServer = context.getBean(NettyWebSocketServer.class);
        nettyWebSocketServer.start();
    }

}
