(ns sueldoneto.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [sueldoneto.core :refer :all]))

(deftest check-test
  (testing "check! resilience"
    (are [expected s v]
         (=  (coerce! s v) expected)
      12000 :sueldoneto.core/annual-gross          "12000"
      12    :sueldoneto.core/installments          "12"
      "A"   :sueldoneto.core/personal-situation    "A"
      44    :sueldoneto.core/age                   "44"
      false :sueldoneto.core/contract              "I"
      "K"   :sueldoneto.core/professional-category "K")))

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
      "Validation failed"                   :sueldoneto.core/professional-category "U")))

(comment
  (run-tests))
