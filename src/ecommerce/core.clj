(ns ecommerce.core
  (:require [ecommerce.products :refer :all]
            [ecommerce.utils :refer :all])
  (:gen-class))

(defn -main
  "Main Function that gets called and generate the relevant csv and json files"
  [& args]
  (let [mods ((read-json-file "lipstick.json") :mods)
        listItems (mods :listItems)]
    (do
      (write-to-csv "first.csv" (prUrl-pr-opr-skus listItems))
      (write-to-csv "second.csv" (oem-sku listItems))
      (spit "third.json" (group-brandName listItems))
      (write-to-csv "fourth.csv" (find-image mods))
      (write-to-csv "fifth.csv" (buy-products listItems 250)))))
