(ns sueldoneto.core
  "Calcula el tipo de retención y el salario neto, en función de la situación laboral y familiar y del IRPF vigente."
  (:require
   [clojure.spec.alpha :as spec]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]
   [expound.alpha :as expound])
  (:gen-class))

(spec/def ::annual-gross pos-int?)
(spec/def ::installments #{12 14})
(spec/def ::age pos-int?)
(spec/def ::personal-situation #{"A" "B" "C"})
(spec/def ::contract #{"G" "M"})
(spec/def ::professional-category #{"A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K"})
(spec/def ::data (spec/keys :req-un
                            [::annual-gross ::installments ::personal-situation ::contract ::age ::professional-category]))

(def data (atom nil))

(defn get-input
  "get user input"
  []
  (let [input (clojure.string/trim (read-line))]
    input))

(defn prompt-personal-situation
  [])

(defn prompt-installments
  [annual-gross]
  (println "Número de pagas:")
  (let [installments (get-input)]
    (swap! data #(assoc % :installments installments))
    (println "pagas" (:installments @data) "sueldo anual: " (:annual-gross @data))))

(defn prompt-annual-gross
  []
  (println "Sueldo bruto anual:")
  (let [annual-gross (get-input)]
    (swap! data #(assoc % :annual-gross annual-gross))
    (prompt-installments annual-gross)))

(defn start-app []
  (println "Calculadora de sueldo neto")
  (println "''''''''''''''''''''''''''")
  (println "")
  (prompt-annual-gross))

(defn -main
  [& args]
  (start-app))
