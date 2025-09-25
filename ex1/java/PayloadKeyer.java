package com.emqx.flink.connector;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class PayloadKeyer implements FlatMapFunction<EMQXMessage<String>, Tuple2<String, Integer>> {
    @Override
    public void flatMap(EMQXMessage<String> msg, Collector<Tuple2<String, Integer>> out) {
        out.collect(new Tuple2<>(msg.payload, 1));
    }
}
