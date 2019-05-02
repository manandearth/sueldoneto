p (ns sueldoneto.core-test
    (:require [clojure.test :refer :all]
              [clojure.spec.alpha :as spec]
              [clojure.spec.gen.alpha :as gen]
              [clojure.spec.test.alpha :as stest]
              [sueldoneto.core :refer :all]
              [sueldoneto.coerce :refer :all]))

(deftest check-test
  (testing "check! resilience"
    (are [expected s v]
         (=  (coerce! s v) expected)
      12000 :sueldoneto.core/annual-gross          "12000"
      12    :sueldoneto.core/installments          "12"
      "A"   :sueldoneto.core/personal-situation    "A"
      44    :sueldoneto.core/age                   "44"
      false :sueldoneto.core/contract              "I"
      "K"   :sueldoneto.core/professional-category "K"
      1     :sueldoneto.core/children              "1"
      1     :sueldoneto.core/young-children        "1"
      true  :sueldoneto.core/exclusivity           "S"
      2     :sueldoneto.core/ancestors             "2"
      1     :sueldoneto.core/old-ancestors         "1"
      0     :sueldoneto.core/disabled-dependents   "0"
      1     :sueldoneto.core/receiving-benefits    "1")))

(deftest check-exception-test
  (testing "check! exceptions"
    (are [expected s v]
         (=  (try (coerce! s v)
                  (catch Exception e (.getMessage e))) expected)
      "For input string: \"MILLION EUROS\"" :sueldoneto.core/annual-gross          "million euros"
      "For input string: \"DOCE\""          :sueldoneto.core/installments          "doce"
      "Validation failed"                   :sueldoneto.core/personal-situation    "F"
      "Validation failed"                   :sueldoneto.core/age                   "-3"
      "Validation failed"                   :sueldoneto.core/contract              "L"
      "Validation failed"                   :sueldoneto.core/professional-category "U"
      "Validation failed"                   :sueldoneto.core/children              "-1"
      "Validation failed"                   :sueldoneto.core/exclusivity           "R"
      "For input string: \"\""              :sueldoneto.core/ancestors             ""
      "For input string: \"D9\""            :sueldoneto.core/old-ancestors         "d9"
      "For input string: \"UNO\""           :sueldoneto.core/disabled-dependents   "uNo"
      "Validation failed"                   :sueldoneto.core/receiving-benefits    "-9")))

(deftest check-young-children-conditional
  (testing "coerce young-children"
    (are [expected v]
         (= (do (swap! data #(assoc % :children 2))
                (try (coerce! :sueldoneto.core/young-children v)
                     (catch Exception e (.getMessage e))))

            expected)
      1                          "1"
      "Validation failed"        "3"
      2                          "2"
      "Validation failed"        "-1"
      0                          "0"
      "For input string: \"NO\"" "no")))

(comment
  (run-tests))

