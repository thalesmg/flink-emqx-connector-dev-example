(ns ex1
  (:import
   (org.apache.flink.streaming.api.environment StreamExecutionEnvironment)
   (org.apache.flink.api.common.eventtime WatermarkStrategy)
   #_(org.apache.flink.api.common.serialization DeserializationSchema)
   #_(org.apache.flink.api.common.typeinfo
    TypeInformation
    TypeHint)
   (com.emqx.flink.connector
    EMQXSource

    StringDeserializer))
  (:gen-class))

#_(def deserializer
  (reify DeserializationSchema
    (deserialize [this ba]
      (String. ba))
    (isEndOfStream [this next-elem]
      false)
    (getProducedType [this]
      (TypeInformation/of (TypeHint.)))))

(defn -main
  [& _args]
  (let [env (StreamExecutionEnvironment/getExecutionEnvironment)
        broker-uri "tcp://deleteme1.emqx.net:1883"
        clientid "cid"
        group-name "gname"
        topic-filter "t/#"
        qos 1
        deserializer (StringDeserializer.)
        emqx-source (EMQXSource. broker-uri clientid group-name topic-filter qos deserializer)
        source (.fromSource env
                            emqx-source
                            (WatermarkStrategy/noWatermarks)
                            "emqx")
        #_#_source (.returns source String)
        _sink (.print source)]
   (.execute env)))
