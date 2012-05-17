(ns langohr-examples.core
  (:gen-class)
  (:import [com.rabbitmq.client Connection Channel AMQP AMQP$Queue$DeclareOk AMQP$Queue$BindOk AMQP$Queue$UnbindOk]
           java.io.IOException)
  (:use clojure.tools.cli
        [langohr.core  :as lhc]
        [langohr.queue :as lhq]
        [langohr.basic :as lhb]
        [langohr.consumers :as lhcons]))

(defonce ^Connection conn (lhc/connect))

(defn run-producer
  []
  (let [channel  (lhc/create-channel conn)
        queue    (.getQueue (lhq/declare channel "test"  :exclusive false))]
    (println "running producer")
    (lhb/publish channel "" queue "payload")
    (println "message published..")
    ))

(defn run-consumer
  []
  (let [channel  (lhc/create-channel conn)
        queue    (.getQueue (lhq/declare channel "test" :exclusive false))
        ]
    (println "running consumer")
    (lhcons/subscribe channel "test" (fn [a b c] (println a b c)) :auto-ack true)))

(defn -main
  [& args]
  (let [ [options] (cli args ["--mode" "Example mode, consumer or producer mode" :default "producer"])]
    (println (:mode options))
    (case (:mode options)
      "producer" (run-producer)
      "consumer" (run-consumer))))
