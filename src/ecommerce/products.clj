(ns ecommerce.products
  (:require [cheshire.core :as jq]
            [clojure.string :refer [split
                                    ends-with?]]
            [ecommerce.utils :refer :all]))

(defn prUrl-pr-opr-skus
  "Get the productUrl, price, originalPrice and num of SKUs"
  [listItems]
  (->> (mapv #(update [(% :productUrl) (% :price) (% :originalPrice) (% :skus)] 3 count) listItems)
       (cons ["productUrl", "price", "originalPrice", "numberOfSKUs"])
       (vec)))

(defn oem-sku
  "Calculate the average price of all products that satisfies these conditions:
   number of ​ skus ​ (for that product) is > 2 and ​ brandName ​ = OEM"
  [listItems]
  (let [filtered-vals (filter #(and (= (% :brandName) "OEM") (> (count (% :skus)) 2))
                              listItems)
        price         (mapv #(Float/parseFloat (% :price)) filtered-vals)
        average-price (try
                        (/ (reduce + price) (count price))
                        (catch Exception e (str "caught exception: " (.getMessage e))))]
    [["Average price"] [average-price]]))

(defn group-brandName
  "group all the products by brandName and count num of products in each brand"
  [listItems]
  (->> (group-by :brandName listItems)
       (map (fn [[k v]] (assoc {} k (count v))))
       (into {})
       (jq/generate-string)))

(defn find-image
  "Find all the image keys values from the json content"
  [input]
  (let [flattened-output (flatten-json nil "_"
                                       (make-map (if (vector? input)
                                                   (zipmap (range) input)
                                                   input)))
        filter-image-keys (filter (fn [[k v]] (ends-with? k ":image")) flattened-output)
        image-vals (distinct (mapv #(vector (last (split (second %) #"/"))) filter-image-keys))]
    (cons ["filename"] image-vals)))

(defn find-products
  "Find the max products which can be bought with the given amount"
  [product-arr sum]
  (loop [curr_sum 0
         result []
         index 0]
    (if (and (< index (count product-arr)) (<= (+ curr_sum ((nth product-arr index) :price)) sum))
      (recur (+ curr_sum ((nth product-arr index) :price))
        (conj result (nth product-arr index)) (inc index))
      (if (and (< index (count product-arr))
               (>= (- sum curr_sum)
                   (- ((nth product-arr index) :price) ((last result) :price))))
        (let [final (conj (butlast result) (nth product-arr index))
              sum   (apply + (map :price final))]
         (recur sum final (inc index)))
        result))))

(defn buy-products
  "Buy the max products from the input list which can be bought with the given amount"
  [listItems sum]
  (let [selectedkeys   (mapv #(select-keys % [:itemId :brandName :price]) listItems)
        update-price   (map (fn [x] (assoc x :price (Float/parseFloat (x :price))))
                            selectedkeys)
        sort-price     (sort-by :price update-price)
        sorted-product (sort-by :price (flatten
                                        (map (fn [[k v]] (conj [] (first v)))
                                             (group-by :brandName sort-price))))
        bought-products (find-products sorted-product sum)
        csv-write       (vec (cons ["itemId" "brandName" "price"]
                                   (mapv #(vec (vals %)) bought-products)))]
    csv-write))
