(ns ex2
  (:import
   (org.apache.flink.streaming.api.environment StreamExecutionEnvironment)
   (org.apache.flink.api.common.eventtime WatermarkStrategy)
   (com.emqx.flink.connector
    EMQXSource

    StringDeserializer
    PayloadKeyer
    FirstElemKeySelector))
  (:gen-class))

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
        stream (-> source
                   (.flatMap (PayloadKeyer.))
                   (.keyBy (FirstElemKeySelector.))
                   (.sum 1))
        _sink (.print stream)]
    (.execute env)))
