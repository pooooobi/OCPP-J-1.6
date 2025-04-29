package com.charger.ocpp.config;

import com.charger.ocpp.message.OcppMessage;
import com.charger.ocpp.message.config.OcppMessageDeserializer;
import com.charger.ocpp.message.config.OcppMessageSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcppConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(OcppMessage.class, new OcppMessageSerializer())
                .registerTypeAdapter(OcppMessage.class, new OcppMessageDeserializer())
                .create();
    }
}
