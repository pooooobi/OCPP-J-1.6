package com.charger.ocpp.message;

import com.charger.ocpp.message.config.OcppMessageDeserializer;
import com.charger.ocpp.message.config.OcppMessageSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.charger.ocpp.message.OcppMessage.MessageType.CALL_ERROR;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractOcppMessageHandler implements OcppMessageHandler {

    private final Gson gson;

    @Override
    public OcppMessage handle(long chargerId, OcppMessage msg) {
        try {
            log.info("[{}] {}", chargerId, gson.toJson(msg));

            OcppMessage response = handleMessage(chargerId, msg);
            log.info("[{}] {}", chargerId, gson.toJson(response));

            return response;
        } catch (Exception e) {
            log.error("[{}] Message handling error: {}", chargerId, e.getMessage());
            e.printStackTrace();

            return createErrorResponse(msg.getUniqueId(), e);
        }
    }

    protected abstract OcppMessage handleMessage(long chargerId, OcppMessage msg);

    protected OcppMessage createErrorResponse(String uniqueId, Exception e) {
        Map<String, String> errorPayload = new HashMap<>();
        errorPayload.put("errorCode", "GenericError");
        errorPayload.put("errorDescription", e.getMessage());
        return new OcppMessage(CALL_ERROR, uniqueId, errorPayload);
    }

    protected OcppMessage createErrorResponse(String uniqueId, String errorMessage) {
        Map<String, String> errorPayload = new HashMap<>();
        errorPayload.put("errorCode", "GenericError");
        errorPayload.put("errorDescription", errorMessage);
        return new OcppMessage(CALL_ERROR, uniqueId, errorPayload);
    }

}
