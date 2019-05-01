(ns sueldoneto.coerce
  (:require
   [clojure.spec.alpha :as spec]))

(def built-in-coercions
  {`::annual-gross #(Integer/parseInt %)
   `::installments #(Integer/parseInt %)
   `::age          #(Integer/parseInt %)
   `::contract #(cond (= % "G") true
                      (= % "I") false)})

(defn coerce [key value]
  (let [coerce-fn (get built-in-coercions key identity)]
    (if (string? value)
      (coerce-fn (clojure.string/upper-case value))
      value)))

(defn check! [spec value]
  (or (spec/valid? spec value)
      (throw (ex-info "Validation failed" {:explanation (spec/explain-str spec value)}))))

(defn coerce!
  [spec v]
  (let [v (coerce spec v)]
    (check! spec v)
    v))
