(ns build
  (:require
   ;; [clojure.pprint :as pp]
   [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-basis
  (b/create-basis
   {:project nil
    :extra {:deps {'com.emqx/flink-emqx-connector
                   {:local/root "../flink-emqx-connector/target/flink-emqx-connector-1.0-SNAPSHOT.jar"}

                   'org.clojure/clojure {:mvn/version "1.12.1"}}}}))

;; (pp/pprint jar-basis)

(defn jar-file
  [lib]
  (format "target/%s.jar" (name lib)))

(defn clean
  [_]
  (b/delete {:path "target"}))

(defn compile-java
  [_]
  (b/javac {:src-dirs ["java"]
            :class-dir class-dir
            :basis basis
            :javac-opts ["--release" "17"]}))

(defn inject-clojure
  [_]
  )

(defn jar
  [{:keys [:lib] :as params}]
  (let [src-dirs [(str lib "/src")]]
    (clean nil)
    (compile-java params)
    (b/copy-dir {:src-dirs src-dirs
                 :target-dir class-dir})
    (b/compile-clj {:basis basis
                    :src-dirs src-dirs
                    :class-dir class-dir})
    (b/uber {:class-dir class-dir
             :uber-file (jar-file lib)
             :basis jar-basis
             :main (symbol lib)})))
