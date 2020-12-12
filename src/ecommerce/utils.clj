(ns ecommerce.utils
  (:require [cheshire.core :as jp]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defn read-json-file
  "Function that reads the json file and parses it"
  [filepath]
  (let [content (slurp filepath)]
    (jp/parse-string content true)))

(defn write-to-csv
  [filename content]
  (with-open [writer (io/writer filename)]
    (csv/write-csv writer content)))

(defn make-map
  "Returns a clojure map, if it finds vectors then maps them with their indices"
  [mp]
  (reduce
    (fn [l [k v]]
      (assoc l k (cond
                   (map? v) (make-map v)
                   (coll? v) (make-map (zipmap (range) v))
                   :else v)))
    {} mp))

(defn flatten-json
  "Flattens the json"
  [prefix separator mp]
  (reduce-kv
    #(let [fld-key (if prefix (str prefix separator %2) %2)]
       (if (map? %3)
         (merge %1 (flatten-json fld-key separator %3))
         (assoc %1 fld-key %3))) {} mp))
