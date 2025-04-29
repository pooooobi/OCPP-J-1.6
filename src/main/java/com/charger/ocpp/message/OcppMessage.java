package com.charger.ocpp.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class OcppMessage {
    private final MessageType messageType;
    private final String uniqueId;
    private final String action;
    private final Object payload;

    public OcppMessage(MessageType messageType, String uniqueId, String action, Object payload) {
        this.messageType = messageType;
        this.uniqueId = uniqueId;
        this.action = action;
        this.payload = payload;
    }

    public OcppMessage(MessageType messageType, String uniqueId, Object payload) {
        this(messageType, uniqueId, null, payload);
    }

    @Getter
    @RequiredArgsConstructor
    public enum MessageType {
        CALL(2),
        CALL_RESULT(3),
        CALL_ERROR(4);

        private final int typeId;

        public static MessageType fromTypeId(int typeId) {
            for (MessageType messageType : MessageType.values()) {
                if (messageType.typeId == typeId) {
                    return messageType;
                }
            }
            throw new IllegalArgumentException("Invalid message type id: " + typeId);
        }
    }
}
