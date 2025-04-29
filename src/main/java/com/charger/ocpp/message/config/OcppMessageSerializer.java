package com.charger.ocpp.message.config;

import com.charger.ocpp.message.OcppMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class OcppMessageSerializer implements JsonSerializer<OcppMessage> {

    @Override
    public JsonElement serialize(OcppMessage msg, Type type, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(msg.getMessageType().getTypeId());
        jsonArray.add(msg.getUniqueId());

        if (msg.getAction() != null) {
            jsonArray.add(msg.getAction());
            jsonArray.add(context.serialize(msg.getPayload()));
        } else {
            jsonArray.add(context.serialize(msg.getPayload()));
        }

        return jsonArray;
    }

}
