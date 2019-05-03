(ns sueldoneto.logic
  (:require
   [sueldoneto.messages :refer [messages]]
   [sueldoneto.coerce :refer :all]))

(def data (atom nil))

;;The cap for annual income's Seguridad Social 3,4% contribution, 
(def max-gross 45014.4)  ;Euros
(def max-s-social (-> max-gross
                      (/ 100)
                      (* 6.35)))

(defn s-social
  [gross]
  (let [percentage (-> gross
                       (/ 100)
                       (* 6.35))]
    (if (< percentage max-s-social)
      percentage
      max-s-social)))
(swap! data #(assoc % :annual-gross 30000 :installments 12))

(defn calc
  []
  (let [{:keys [annual-gross installments]} @data
        s-social-contribution (s-social annual-gross)
        annual-net (-> annual-gross
                       (- s-social-contribution))
        net-wage (/ annual-net installments)]
    (str "\nCotización Seguridad Social (anual): " s-social-contribution " Euros ,"
         "\nSueldo neto: " net-wage " por " installments " pagas,")))
