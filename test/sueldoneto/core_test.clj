(ns sueldoneto.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [sueldoneto.core :refer :all]
            [sueldoneto.coerce :refer :all]
            [sueldoneto.logic :refer :all]))

(deftest check-test
  (testing "check! resilience"
    (are [expected s v]
         (=  expected
             (coerce! s v))
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
         (= expected (try (coerce! s v)
                          (catch Exception e (.getMessage e))))
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
         (= expected
            (do (swap! data #(assoc % :children 2))
                (try (coerce! :sueldoneto.core/young-children v)
                     (catch Exception e (.getMessage e)))))
      1                          "1"
      "Validation failed"        "3"
      2                          "2"
      "Validation failed"        "-1"
      0                          "0"
      "For input string: \"NO\"" "no")))

;;-------------------
;;ns sueldoneto.logic
;;-------------------
(deftest max-s-social-test
  (are [expected stub]
       (= expected
          (do (swap! data #(assoc % :contract stub))
              (max-s-social)))
    (* 45014.4 0.0635) true
    (* 45014.4 0.064) false))

(deftest s-social-test
  (are [expected annual-gross contract]
       (= expected
          (do (swap! data #(assoc % :contract contract :annual-gross annual-gross))
              (s-social)))
    1280.0 20000 false
    1270.0 20000 true
    1920.0 30000 false
    1905.0 30000 true
    0.0    0     true))

(deftest disability-allowance-test
  (are [expected disability annual-gross]
       (= expected
          (do (swap! data #(assoc % :disability disability :annual-gross annual-gross))
              (disability-allowance)))
    2000       "A" 20000
    5700       "A" 10000
    2000       "A" 50000
    5500       "B" 20000
    8138.5625  "B" 13000
    5500       "B" 80000
    9200       "B" 1000
    9200       "B" 0
    13450      "C" 1000
    12388.5625 "C" 13000
    9750       "C" 30000
    9750       "C" 50000))

(deftest children-allowance-test
  (are [expected children young-children]
       (= expected
          (do (swap! data #(assoc % :children children :young-children young-children))
              (children-allowance)))
    0     0 0
    2400  1 0
    5100  2 0
    9100  3 0
    13600 4 0
    18100 5 0
    22600 6 0
    5200  1 1
    7900  2 1
    10700 2 2
    26500 5 3))

(deftest disabled-dependants-allowance-test
  (are [expected ancestors children m-grade-disabled-ancestors m-grade-disabled-descendants h-grade-disabled-ancestors h-grade-disabled-descendants]
       (= expected
          (do (swap! data #(assoc % :children children :ancestors ancestors :m-grade-disabled-ancestors m-grade-disabled-ancestors :m-grade-disabled-descendants m-grade-disabled-descendants :h-grade-disabled-ancestors h-grade-disabled-ancestors :h-grade-disabled-descendants h-grade-disabled-descendants))
              (disabled-dependants-allowance)))
    0     0 0 0 0 0 0
    0     1 1 0 0 0 0
    3000  1 1 1 0 0 0
    6000  1 1 2 0 0 0
    6000  1 1 1 1 0 0
    9000  2 1 1 2 0 0
    9000  0 1 0 0 1 0
    18000 0 2 0 0 2 0
    21000 2 1 0 1 2 0
    9000  0 1 0 0 0 1))

(comment
  (run-tests))

;;Helper in building tests
(comment
  (= (do (swap! data #(assoc % :children 1 :ancestors 2 :m-grade-disabled-ancestors 0 :m-grade-disabled-descendants 1 :h-grade-disabled-ancestors 2 :h-grade-disabled-descendants 0))
         (disabled-dependants-allowance)) 0))
