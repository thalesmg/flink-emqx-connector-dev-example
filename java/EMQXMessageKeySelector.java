package com.emqx.flink.connector;

import org.apache.flink.api.java.functions.KeySelector;

public class EMQXMessageKeySelector implements KeySelector<EMQXMessage<String>, String> {
    @Override
    public String getKey(EMQXMessage<String> emqxMessage) {
        return emqxMessage.payload;
    }
}
