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

(def data (atom nil))

(spec/def ::annual-gross pos-int?)
(spec/def ::installments #{12 14})
(spec/def ::age pos-int?)
(spec/def ::personal-situation #{"A" "B" "C"})
(spec/def ::contract boolean?)
(spec/def ::professional-category #{"A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K"})
(spec/def ::children  nat-int?)
(spec/def ::young-children  (spec/and #(>= (:children @data) %) nat-int?))
(spec/def ::exclusivity boolean?)
(spec/def ::ancestors nat-int?)
(spec/def ::old-ancestors nat-int?)
(spec/def ::disabled-dependents nat-int?)
(spec/def ::receiving-benefits nat-int?)
(spec/def ::m-grade-disabled-descendants (spec/and
                                          #(>= (:children @data) %)
                                          nat-int?))

(spec/def ::m-grade-disabled-ancestors (spec/and
                                        #(>= (+ (:ancestors @data) (:old-ancestors @data)) %)
                                        nat-int?))

(spec/def ::h-grade-disabled-descendants (spec/and
                                          #(>= (- (:children @data) (:m-grade-disabled-descendants @data)) %)
                                          nat-int?))
(spec/def ::h-grade-disabled-ancestors (spec/and
                                        #(>= (- (+ (:ancestors @data) (:old-ancestors @data)) (:m-grade-disabled-ancestors @data)) %)
                                        nat-int?))
(spec/def ::disability #{"A" "B" "C"})

(spec/def ::data (spec/keys :req-un
                            [::annual-gross ::installments ::personal-situation ::contract ::age ::professional-category ::children ::young-children ::exclusivity ::ancestors ::old-ancestors ::disabled-dependents ::receiving-benefits ::m-grade-disabled-descendants ::m-grade-disabled-ancestors ::h-grade-disabled-descendants ::h-grade-disabled-ancestors ::disability]))

(defn get-input
  "get user input"
  []
  (let [input (clojure.string/trim (read-line))]
    input))

(defn ppdata [db]
  (do (println (array-map "Pagas"                                      (:installments @db)
                          "\nSueldo anual: "                           (:annual-gross @db)
                          "\nSituación familiar:"                      (:personal-situation @db)
                          "\nEdad:"                                    (:age @db)
                          "\ncontrato"                                 (:contract @db)
                          "\nProfessional category:"                   (:professional-category @db)
                          "\nNiños"                                    (:children @db)
                          "\nNiños menores a 3 años"                   (:young-children @db)
                          "\nEn exclusiva"                             (:exclusivity @db)
                          "\nAscendientes"                             (:ancestors @db)
                          "\nMayores ascendientes"                     (:old-ancestors @db)
                          "\nDescapacitados"                           (:disabled-dependents @db)
                          "\nContribuyentes con minimos"               (:receiving-benefits @db)
                          "\nDescendientes discapacitados 33% y a 65%" (:m-grade-disabled-descendants @db)
                          "\nAscendientes discapacitados 33% a 65%"    (:m-grade-disabled-ancestors @db)
                          "\nDescendientes discapacitados al 65%"      (:h-grade-disabled-descendants @db)
                          "\nAscendientes discapacitados al 65%"       (:h-grade-disabled-ancestors @db)
                          "\nDiscapacidad"                             (:disability @db)))))

(defn prompt-disability
  []
  (println (get-in messages [:disability :pre]))
  (try
    (let [disability (coerce! ::disability (get-input))]
      (swap! data #(assoc % :disability disability))
      (ppdata data))
    (catch Exception _ (println (get-in messages [:disability :error]))
           (prompt-disability))))

(defn prompt-h-grade-disabled-ancestors
  []
  (println (get-in messages [:h-grade-disabled-ancestors :pre]))
  (try
    (let [h-grade-disabled-ancestors (coerce! ::h-grade-disabled-ancestors (get-input))]
      (swap! data #(assoc % :h-grade-disabled-ancestors h-grade-disabled-ancestors))
      (prompt-disability))
    (catch Exception e (if (= (.getMessage e) "Vlidation failed")
                         (println (get-in messages [:h-grade-disabled-ancestors :alt-error]))
                         (println (get-in messages [:h-grade-disabled-ancestors :error])))
           (prompt-h-grade-disabled-ancestors))))

(defn prompt-h-grade-disabled-descendants
  []
  (println (get-in messages [:h-grade-disabled-descendants :pre]))
  (try
    (let [h-grade-disabled-descendants (coerce! ::h-grade-disabled-descendants (get-input))]
      (swap! data #(assoc % :h-grade-disabled-descendants h-grade-disabled-descendants))
      (prompt-h-grade-disabled-ancestors))
    (catch Exception e (if (= (.getMessage e) "Validation failed")
                         (println (get-in messages [:h-grade-disabled-descendants :alt-error]))
                         (println (get-in messages [:h-grade-disabled-descendants :error])))
           (prompt-h-grade-disabled-descendants))))

(defn prompt-m-grade-disabled-ancestors
  []
  (println (get-in messages [:m-grade-disabled-ancestors :pre]))
  (try
    (let [m-grade-disabled-ancestors (coerce! ::m-grade-disabled-ancestors (get-input))]
      (swap! data #(assoc % :m-grade-disabled-ancestors m-grade-disabled-ancestors))
      (prompt-h-grade-disabled-descendants))
    (catch Exception e (if (= (.getMessage e) "Validation failed")
                         (println (get-in messages [:m-grade-disabled-ancestors :alt-error]))
                         (println (get-in messages [:m-grade-disabled-ancestors :error])))
           (prompt-m-grade-disabled-ancestors))))

(defn prompt-m-grade-disabled-descendants
  []
  (println (get-in messages [:m-grade-disabled-descendants :pre]))
  (try
    (let [m-grade-disabled-descendants (coerce! ::m-grade-disabled-descendants (get-input))]
      (swap! data #(assoc % :m-grade-disabled-descendants m-grade-disabled-descendants))
      (prompt-m-grade-disabled-ancestors))
    (catch Exception e (if (= (.getMessage e) "Validation failed")
                         (println (get-in messages [:m-grade-disabled-descendants :alt-error]))
                         (println (get-in messages [:m-grade-disabled-descendants :error])))
           (prompt-m-grade-disabled-descendants))))

(defn prompt-receiving-benefits
  []
  (println (get-in messages [:receiving-benefits :pre]))
  (try
    (let [receiving-benefits (coerce! ::receiving-benefits (get-input))]
      (swap! data #(assoc % :receiving-benefits receiving-benefits))
      (prompt-m-grade-disabled-descendants))
    (catch Exception _ (println (get-in messages [:receiving-benefits :error]))
           (prompt-receiving-benefits))))

(defn prompt-disabled-dependents
  []
  (println (get-in messages [:disabled-dependents :pre]))
  (try
    (let [disabled-dependents (coerce! ::disabled-dependents (get-input))]
      (swap! data #(assoc % :disabled-dependents disabled-dependents))
      (prompt-receiving-benefits))
    (catch Exception _ (println (get-in messages [:disabled-dependents :error]))
           (prompt-disabled-dependents))))

(defn prompt-old-ancestors
  []
  (println (get-in messages [:old-ancestors :pre]))
  (try
    (let [old-ancestors (coerce! ::old-ancestors (get-input))]
      (swap! data #(assoc % :old-ancestors old-ancestors))
      (prompt-disabled-dependents))
    (catch Exception _ (println (get-in messages [:old-ancestors :error]))
           (prompt-old-ancestors))))

(defn prompt-ancestors
  []
  (println (get-in messages [:ancestors :pre]))
  (try
    (let [ancestors (coerce! ::ancestors (get-input))]
      (swap! data #(assoc % :ancestors ancestors))
      (prompt-old-ancestors))
    (catch Exception _ (println (get-in messages [:ancestors :error]))
           (prompt-ancestors))))

(defn prompt-exclusivity
  []
  (mapv println (get-in messages [:exclusivity :pre]))
  (try
    (let [exclusivity (coerce! ::exclusivity (get-input))]
      (swap! data #(assoc % :exclusivity exclusivity))
      (prompt-ancestors))
    (catch Exception _ (println (get-in messages [:exclusivity :error]))
           (prompt-exclusivity))))

(defn prompt-young-children
  []
  (println (get-in messages [:young-children :pre]))
  (try
    (let [young-children (coerce! ::young-children (get-input))]
      (swap! data #(assoc % :young-children young-children))
      (prompt-exclusivity))
    (catch Exception e (if (= (.getMessage e) "Validation failed")
                         (println (get-in messages [:young-children :alt-error]))
                         (println (get-in messages [:young-children :error])))
           (prompt-young-children))))

(defn prompt-children
  []
  (println (get-in messages [:children :pre]))
  (try
    (let [children (coerce! ::children (get-input))]
      (swap! data #(assoc % :children children))
      (prompt-young-children))
    (catch Exception _ (println (get-in messages [:children :error]))
           (prompt-children))))

(defn prompt-professional-category
  []
  (mapv println (get-in messages [:professional-category :pre]))
  (try
    (let [professional-category (coerce! ::professional-category (get-input))]
      (swap! data #(assoc % :professional-category professional-category))
      (prompt-children))
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
    (let [installments (coerce! ::installments (get-input))]
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
