package com.charger.ocpp.message.config;

import com.charger.ocpp.message.OcppMessage;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.charger.ocpp.message.OcppMessage.MessageType.fromTypeId;

public class OcppMessageDeserializer implements JsonDeserializer<OcppMessage> {

    @Override
    public OcppMessage deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();
        int messageType = jsonArray.get(0).getAsInt();
        String uniqueId = jsonArray.get(1).getAsString();

        if (jsonArray.size() == 4) {
            String action = jsonArray.get(2).getAsString();
            Object payload = context.deserialize(jsonArray.get(3), Object.class);
            return new OcppMessage(fromTypeId(messageType), uniqueId, action, payload);
        } else {
            Object payload = context.deserialize(jsonArray.get(2), Object.class);
            return new OcppMessage(fromTypeId(messageType), uniqueId, payload);
        }
    }

}