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

;; docker exec -it dev-emqx-1 emqx remote_console
;; publisher = spawn_link(fn -> Stream.interval(800) |> Stream.each(fn _ -> x = Enum.random(["a", "b", "c"]); :emqx_message.make("t/#{x}", x) |> :emqx.publish() end) |> Stream.run() end)
(defn -main
  [& _args]
  (let [env (StreamExecutionEnvironment/getExecutionEnvironment)
        broker-host "deleteme1.emqx.net"
        broker-port 1883
        clientid "cid"
        group-name "gname"
        topic-filter "t/#"
        qos 1
        deserializer (StringDeserializer.)
        emqx-source (EMQXSource. broker-host broker-port clientid
                                 group-name topic-filter qos deserializer)
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
