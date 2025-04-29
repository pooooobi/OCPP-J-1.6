package com.charger.ocpp.message;

public interface OcppMessageHandler {
    OcppMessage handle(long chargerId, OcppMessage msg);
}
