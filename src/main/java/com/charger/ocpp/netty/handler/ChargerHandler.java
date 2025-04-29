package com.charger.ocpp.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public class ChargerHandler extends ChannelInboundHandlerAdapter {

    public static final AttributeKey<Long> CHARGER_ID_ATTRIBUTE = AttributeKey.valueOf("chargerId");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            ctx.channel().attr(CHARGER_ID_ATTRIBUTE).set(extractChargerId(((FullHttpRequest) msg).uri()));
        }
        ctx.fireChannelRead(msg);
    }

    private Long extractChargerId(String uri) {
        String[] parts = uri.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}