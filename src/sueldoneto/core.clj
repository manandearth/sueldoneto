(ns sueldoneto.core
  "Calcula el tipo de retención y el salario neto, en función de la situación laboral y familiar y del IRPF vigente."
  (:require
   [clojure.spec.alpha :as spec]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [expound.alpha :as expound]
   [sueldoneto.messages :refer [messages]]
   [sueldoneto.coerce :refer :all])
  (:gen-class))

(spec/def ::annual-gross pos-int?)
(spec/def ::installments #{12 14})
(spec/def ::age pos-int?)
(spec/def ::personal-situation #{"A" "B" "C"})
(spec/def ::contract boolean?)
(spec/def ::professional-category #{"A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K"})
(spec/def ::data (spec/keys :req-un
                            [::annual-gross ::installments ::personal-situation ::contract ::age ::professional-category]))

(def data (atom nil))

(defn get-input
  "get user input"
  []
  (let [input (clojure.string/trim (read-line))]
    input))

(defn prompt-professional-category
  []
  (mapv println (get-in messages [:professional-category :pre]))
  (try
    (let [professional-category (coerce! ::professional-category (get-input))]
      (swap! data #(assoc % :professional-category professional-category))
      (println "pagas" (:installments @data) "sueldo anual: " (:annual-gross @data) "Situación familiar:" (:personal-situation @data) "Edad:" (:age @data) "contracto" (:contract @data) "Professional category:" (:professional-category @data)))
    (catch Exception _ (println (get-in messages [:professional-category :error]))
           (prompt-professional-category))))

(defn prompt-contract
  []
  (mapv println (get-in messages [:contract :pre]))
  (try
    (let [contract (coerce! ::contract (get-input))]
      (swap! data #(assoc % :contract contract))
      (prompt-professional-category))
    (catch Exception _ (println (get-in messages [:contract :error]))
           (prompt-contract))))

(defn prompt-age
  []
  (println (get-in messages [:age :pre]))
  (try
    (let [age (coerce! ::age (get-input))]
      (swap! data #(assoc % :age age))
      (prompt-contract))
    (catch Exception _ (println (get-in messages [:age :error]))
           (prompt-age))))

(defn prompt-personal-situation
  []
  (mapv println (get-in messages [:personal-situation :pre]))
  (try
    (let [personal-situation (coerce! ::personal-situation (get-input))]
      (swap! data #(assoc % :personal-situation personal-situation))
      (prompt-age))
    (catch Exception _ (println (get-in messages [:personal-situation :error]))
           (prompt-personal-situation))))

(defn prompt-installments
  []
  (println (get-in messages [:installments :pre]))
  (try
    (let [installments (coerce ::installments (get-input))]
      (swap! data #(assoc % :installments installments))
      (prompt-personal-situation))
    (catch Exception _ (println (get-in messages [:installments :error]))
           (prompt-installments))))

(defn prompt-annual-gross
  []
  (println (get-in messages [:annual-gross :pre]))
  (try
    (let [annual-gross (coerce! ::annual-gross (get-input))]
      (swap! data #(assoc % :annual-gross annual-gross))
      (prompt-installments))
    (catch Exception _ (println (get-in messages [:annual-gross :error]))
           (prompt-annual-gross))))

(defn start-app []
  (println "Calculadora de sueldo neto")
  (println "''''''''''''''''''''''''''")
  (println "")
  (prompt-annual-gross))

(defn -main
  [& args]
  (start-app))
