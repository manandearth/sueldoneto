(ns sueldoneto.coerce
  (:require
   [clojure.spec.alpha :as spec]))

(def built-in-coercions
  {`:sueldoneto.core/annual-gross   #(Integer/parseInt %)
   `:sueldoneto.core/installments   #(Integer/parseInt %)
   `:sueldoneto.core/age            #(Integer/parseInt %)
   `:sueldoneto.core/contract       #(cond (= % "G") true
                                           (= % "I") false)
   `:sueldoneto.core/children       #(Integer/parseInt %)
   `:sueldoneto.core/young-children #(Integer/parseInt %)})

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

