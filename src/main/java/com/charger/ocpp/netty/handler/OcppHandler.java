package com.charger.ocpp.netty.handler;

import com.charger.ocpp.message.OcppMessage;
import com.charger.ocpp.message.OcppMessageHandler;
import com.charger.ocpp.message.factory.OcppMessageHandlerFactory;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OcppHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final Gson gson;
    private final OcppMessageHandlerFactory handlerFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Long chargerId = ctx.channel().attr(ChargerHandler.CHARGER_ID_ATTRIBUTE).get();

        try {
            OcppMessage ocppMessage = gson.fromJson(msg.text(), OcppMessage.class);

            switch (ocppMessage.getMessageType()) {
                case CALL:
                    OcppMessageHandler handler = handlerFactory.getHandler(ocppMessage.getAction());

                    if (handler != null) {
                        OcppMessage response = handler.handle(chargerId, ocppMessage);
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(gson.toJson(response)));
                    }
                    break;
                case CALL_RESULT:
                    log.info("[{}] {}", chargerId, msg.text());
                    break;
                case CALL_ERROR:
                    log.error("[{}] {}", chargerId, msg.text());
                    break;
            }
        } catch (Exception e) {
            log.error("[{}] {}", chargerId, e.getMessage());
            e.printStackTrace();
        }
    }

}
