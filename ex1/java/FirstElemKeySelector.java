package com.emqx.flink.connector;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.functions.KeySelector;

public class FirstElemKeySelector implements KeySelector<Tuple2<String, Integer>, String> {
    @Override
    public String getKey(Tuple2<String, Integer> tuple) {
        return tuple.f0;
    }
}
