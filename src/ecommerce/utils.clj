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
