(ns build
  (:require [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))

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
    (b/jar {:class-dir class-dir
            :jar-file (jar-file lib)
            :basis basis
            :main (symbol lib)})))
