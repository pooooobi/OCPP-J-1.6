package com.charger.ocpp.netty;

import com.charger.ocpp.netty.handler.ChargerHandler;
import com.charger.ocpp.netty.handler.OcppHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class NettyWebSocketServer implements ApplicationListener<ContextClosedEvent> {

    private final OcppHandler ocppHandler;
    private final ChargerHandler chargerHandler;

    @Value("${ocpp.port}")
    private int port;

    @Value("${ocpp.path}")
    private String path;

    @Value("${ocpp.bossGroupThreads}")
    private int bossGroupThreads;

    @Value("${ocpp.workerGroupThreads}")
    private int workerGroupThreads;

    // Netty
    private EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupThreads);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(workerGroupThreads);
    private Channel ch;
    ChannelPipeline pipeline;

    public void start() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            pipeline = ch.pipeline();

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            pipeline.addLast(new IdleStateHandler(0, 0, 360, TimeUnit.SECONDS));
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast(new WebSocketServerProtocolHandler(path, null ,true));
                            pipeline.addLast(chargerHandler);
                            pipeline.addLast(ocppHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ch = serverBootstrap.bind(port).sync().channel();
        } catch (Exception e) {
            log.error("OCPP Server initialized error: {}", e.getMessage());
            e.printStackTrace();
        }

        log.info("OCPP Server started on port: {}", port);
    }

    // implements ApplicationListener<ContextClosedEvent> 대신 @PreDestroy로 대체 가능
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("OCPP Server stopping...");

        if (ch != null) {
            ch.close();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        log.info("OCPP Server stopped");
    }
}
