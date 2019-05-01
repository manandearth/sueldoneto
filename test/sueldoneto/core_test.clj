(ns sueldoneto.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [sueldoneto.core :refer :all]))

(deftest check-test
  (testing "check! resilience"
    (are [expected spec v]
         (=  (coerce! spec v) expected)
      12000 :sueldoneto.core/annual-gross          "12000"
      12    :sueldoneto.core/installments          "12"
      "A"   :sueldoneto.core/personal-situation    "A"
      44    :sueldoneto.core/age                   "44"
      false :sueldoneto.core/contract              "I"
      "K"   :sueldoneto.core/professional-category "K")))

(comment
  (run-tests))
