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
(spec/def ::contract boolean?)
(spec/def ::professional-category #{"A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K"})
(spec/def ::data (spec/keys :req-un
                            [::annual-gross ::installments ::personal-situation ::contract ::age ::professional-category]))

(def built-in-coercions
  {`::annual-gross #(Integer/parseInt %)
   `::installments #(Integer/parseInt %)
   `::age          #(Integer/parseInt %)
   `::contract #(if (= % "G") true false)})

(defn coerce [key value]
  (let [coerce-fn (get built-in-coercions key identity)]
    (if (string? value)
      (coerce-fn (clojure.string/upper-case value))
      value)))

(defn check! [& args]
  (doseq [[spec x] (partition 2 args)]
    (or (spec/valid? spec x)
        (do
          (expound/expound spec x)
          (throw (ex-info "Validation failed" {:explanation (spec/explain-str spec x)})))))
  true)

(defn coerce!
  [spec v]
  (let [v (coerce spec v)]
    (check! spec v)
    v))

(def data (atom nil))

(defn get-input
  "get user input"
  []
  (let [input (clojure.string/trim (read-line))]
    input))

(defn prompt-professional-category
  []
  (println "Categoría profesional:")
  (println "[A] Ingenieros y Licenciados")
  (println "[B] Ingenieros Técnicos, Peritos y Ayudantes Titulados")
  (println "[C] Jefes Administrativos y de Taller")
  (println "[D] Ayudantes no Titulados")
  (println "[E] Oficiales Administrativos")
  (println "[F] Subalternos")
  (println "[G] Auxiliares Administrativos")
  (println "[H] Oficiales de primera y segunda")
  (println "[I] Oficiales de tercera y Especialistas")
  (println "[J] Peones")
  (println "[K] Trabajadores menores de dieciocho años, cualquiera")
  (let [professional-category (coerce! ::professional-category (get-input))]
    (swap! data #(assoc % :professional-category professional-category))
    (println "pagas" (:installments @data) "sueldo anual: " (:annual-gross @data) "Situación familiar:" (:personal-situation @data) "Edad:" (:age @data) "contracto" (:contract @data) "Professional category:" (:professional-category @data))))

(defn prompt-contract
  []
  (println "Tipo de contrato laboral:")
  (println "[G] general [I] Duración inferior a doce meses")
  (let [contract (coerce! ::contract (get-input))]
    (swap! data #(assoc % :contract contract))
    (prompt-professional-category)))

(defn prompt-age
  []
  (println "Edad:")
  (let [age (coerce! ::age (get-input))]
    (swap! data #(assoc % :age age))
    (prompt-contract)))

(defn prompt-personal-situation
  []
  (println "Situación familiar:")
  (println "[A] Soltero, viudo, divorciado o separado con hijos a cargo")
  (println "[B] Casado y cuyo cónyuge no obtiene rentas superiores a 1.500 euros anuales")
  (println "[C] Otros")
  (let [personal-situation (coerce! ::personal-situation (get-input))]
    (swap! data #(assoc % :personal-situation personal-situation))
    (prompt-age)))

(defn prompt-installments
  []
  (println "Número de pagas:")
  (let [installments (coerce ::installments (get-input))]
    (swap! data #(assoc % :installments installments))
    (prompt-personal-situation)))

(defn prompt-annual-gross
  []
  (println "Sueldo bruto anual:")
  (let [annual-gross (coerce! ::annual-gross (get-input))]
    (swap! data #(assoc % :annual-gross annual-gross))
    (prompt-installments)))

(defn start-app []
  (println "Calculadora de sueldo neto")
  (println "''''''''''''''''''''''''''")
  (println "")
  (prompt-annual-gross))

(defn -main
  [& args]
  (start-app))
