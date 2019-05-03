(ns sueldoneto.logic
  (:require
   [sueldoneto.messages :refer [messages]]
   [sueldoneto.coerce :refer :all]))

(def data (atom nil))

;;The cap for annual income's Seguridad Social 3,4% contribution, 
(def max-gross 45014.4)  ;Euros
(defn max-s-social
  [contract]
  (-> max-gross
      (/ 100)
      (* 6.35)))

(defn s-social
  [gross contract]
  (let [percentage  (-> gross
                        (/ 100)
                        (* (if contract 6.35 6.4)))]
    (if (< percentage (max-s-social contract))
      percentage
      max-s-social)))

(defn calc
  []
  (let [{:keys [annual-gross installments contract]} @data
        s-social-contribution (s-social annual-gross contract)
        annual-net (-> annual-gross
                       (- s-social-contribution))
        net-wage (/ annual-net installments)]
    {:net-wage net-wage :s-social-contribution s-social-contribution}))

