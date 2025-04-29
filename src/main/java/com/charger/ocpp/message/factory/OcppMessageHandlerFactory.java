package com.charger.ocpp.message.factory;

import com.charger.ocpp.message.OcppMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OcppMessageHandlerFactory {

    private final Map<String, OcppMessageHandler> handlerMap = new HashMap<>();

    public OcppMessageHandlerFactory(List<OcppMessageHandler> handlers) {
        for (OcppMessageHandler handler : handlers) {
            String actionType = getActionTypeFromHandlerName(handler.getClass().getSimpleName());
            if (actionType != null) {
                handlerMap.put(actionType.toLowerCase(), handler);
                log.info("OcppMessageHandlerFactory Registered > {}", actionType);
            }
        }
    }

    private String getActionTypeFromHandlerName(String handlerName) {
        if (handlerName.endsWith("Handler")) {
            return handlerName.substring(0, handlerName.length() - "Handler".length()).toLowerCase();
        }
        return null;
    }

    public OcppMessageHandler getHandler(String actionType) {
        OcppMessageHandler handler = handlerMap.getOrDefault(actionType.toLowerCase(), null);

        if (handler == null) {
            log.error("OcppMessageHandlerFactory Handler not found > {}", actionType);
        }
        return handler;
    }
}
