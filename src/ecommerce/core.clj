(ns ecommerce.core
  (:require [ecommerce.products :refer :all]
            [ecommerce.utils :refer :all])
  (:gen-class))

(defn -main
  "Main Function that gets called and generate the relevant csv and json files"
  [& args]
  (let [json-content (read-json-file "lipstick.json")
        listItems (get-in json-content [:mods :listItems])]
    (do
      (write-to-csv "first.csv" (prUrl-pr-opr-skus listItems))
      (write-to-csv "second.csv" (oem-sku listItems))
      (spit "third.json" (group-brandName listItems))
      (write-to-csv "fourth.csv" (find-image json-content))
      (write-to-csv "fifth.csv" (buy-products listItems 250)))))
