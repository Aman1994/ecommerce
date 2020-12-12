(ns ecommerce.products-test
  (:require [clojure.test :refer :all]
            [ecommerce.products :refer :all]))

(def listItems [{:productUrl "//www.lazada.com"
                 :price "20"
                 :originalPrice "30"
                 :brandName "OEM"
                 :skus [{"id" "224295407_TH-946252295"}
                        {"id" "224295407_TH-946252296"}
                        {"id" "224295407_TH-946252296"}]}
                {:productUrl "//www.terx.com"
                 :price "200"
                 :originalPrice "300"
                 :brandName "OEM"
                 :skus [{"id" "224295407_TH-946252295"}
                        {"id" "224295407_TH-946252296"}
                        {"id" "224295407_TH-946252296"}]}
                {:productUrl "//www.abc.com"
                 :price "100"
                 :originalPrice "200"
                 :brandName "XYZ"
                 :skus [{"id" "224295407_TH-946252295"}
                        {"id" "224295407_TH-946252296"}
                        {"id" "224295407_TH-946252291"}]}])

(deftest test-productUrl-pr-opr-sku
  (testing "prUrl-pr-opr-skus function"
    (is (= [["productUrl" "price" "originalPrice" "numberOfSKUs"] ["//www.lazada.com" "20" "30" 3] ["//www.terx.com" "200" "300" 3] ["//www.abc.com" "100" "200" 3]]
           (prUrl-pr-opr-skus listItems)))))

(deftest test-oem-sku
  (testing "oem-sku function"
    (is (= [["Average price"] [110.0]] (oem-sku listItems)))
    (is (= [["Average price"] ["caught exception: Divide by zero"]]
           (oem-sku [{:productUrl "//www.lazada.com"
                      :price "20"
                      :originalPrice "30"
                      :brandName "abc"
                      :skus [{"id" "224295407_TH-946252295"}
                             {"id" "224295407_TH-946252296"}
                             {"id" "224295407_TH-946252296"}]}])))))

(deftest test-group-brandName
  (testing "group-brandName function"
    (is (= "{\"OEM\":2,\"XYZ\":1}" (group-brandName listItems)))
    (is (= "{\"\":1}" (group-brandName [{:br 1}])))))

(deftest test-buy-products
  (testing "test buy-products"
    (is (= [["itemId" "brandName" "price"] ["OEM" 20.0] ["XYZ" 100.0]]
           (buy-products listItems 200)))))
