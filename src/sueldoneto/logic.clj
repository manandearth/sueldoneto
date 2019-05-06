(ns sueldoneto.logic
  (:require
   [sueldoneto.messages :refer [messages]]
   [sueldoneto.coerce :refer :all]))

(def data (atom nil))

;;stub -> uncomment in development

(comment
  (swap! data #(assoc % :annual-gross 30000 :installments 12 :disability "B" :contract true :professional-category "C" :children 3 :young-children 1 :ancestors 1 :old-ancestors 1)))

;;The cap for annual income's Seguridad Social 6.4% contribution, 

(def max-gross 45014.4)  ;Euros
(def general-contract-rate 0.0635)
(def inferior-contract-rate 0.064)

(defn max-s-social []
  (let [{:keys [contract]} @data]
    (-> max-gross
        (* (if contract
             general-contract-rate
             inferior-contract-rate)))))

(comment ;TODO find a more interesting implemntation of this
  (defn cuota-mensual-pagar []
    (let [{:keys [annual-gross professional-category]} @data
          datos (case professional-category
                  "A" {:min 1052.9 :max 3751.2}
                  "B" {:min 956.1 :max 3751.2}
                  "C" {:min 831.6 :max 3751.2}
                  "D" {:min 825.6 :max 3751.2}
                  "E" {:min 825.6 :max 3751.2}
                  "F" {:min 825.6 :max 3751.2}
                  "G" {:min 825.6 :max 3751.2}
                  "H" {:min 825.6 :max 3751.2}
                  "I" {:min 825.6 :max 3751.2}
                  "J" {:min 825.6 :max 3751.2}
                  "K" {:min 825.6 :max 3751.2})]
      (if (< (/ annual-gross 12) (:min datos))
        (* 0.0635 (:min datos))
        (if (> (/ annual-gross 12) (:max datos))
          (* 0.0635 (:min datos))
          (* (/ annual-gross 12) 0.0635))))))

(defn s-social
  []
  (let [{:keys [annual-gross contract]} @data
        percentage  (-> annual-gross
                        (* (if contract
                             general-contract-rate
                             inferior-contract-rate)))]
    (if (< percentage (max-s-social))
      percentage
      (max-s-social))))

(defn disability-allowance []
  (let [{:keys [disability annual-gross]} @data
        net-profit (- annual-gross (s-social))
        base-exemption 2000
        net-reduction (cond
                        (< net-profit 11250) 3700
                        (>= net-profit 14450) 0
                        :else (- 3700 (* 1.15625 (- net-profit 11250))))
        disability-reduction (case disability
                               "A"  0
                               "B"  3500
                               "C"  7750)]
    (+ base-exemption net-reduction disability-reduction)))

(defn children-allowance
  []
  (let [{:keys [children young-children]} @data
        extra-children (- children 4)
        allowance (case children
                    0 0
                    1 2400
                    2 5100
                    3 9100
                    4 13600
                    (+ 13600 (* 4500 extra-children)))]
    (+ allowance (* 2800 young-children))))

(defn ancestor-allowance
  []
  (let [{:keys [annual-gross ancestors old-ancestors]} @data
        net-profit (- annual-gross (s-social))]
    (if (< net-profit 8000)
      (+ (* ancestors 1150) (* old-ancestors 2550))
      0)))

(def total-allowance (+ (ancestor-allowance) (children-allowance) (disability-allowance)))

(defn family-situation-exemption
  []
  (let [{:keys [children personal-situation]} @data]
    (cond
      (= "B" personal-situation) (case children
                                   0 13696
                                   1 14985
                                   17138)
      (= "C" personal-situation) (case children
                                   0 12000
                                   1 12607
                                   13275)
      :else (case children
              0 0
              1 14266
              15803))))

(defn irpf
  []
  (let [{:keys [annual-gross]} @data
        net-profit (- annual-gross (s-social))
        base (- net-profit total-allowance)
        a (if (<= base 12450) base 12450)
        b (if (<= base 20200) 0 (- base 12450))
        c (if (<= base 35200) 0 (- base 20200))
        d (if (<= base 60000) 0 (- base 35200))
        e (if (> base 60000) (- base 60000) 0)]
    (if (< net-profit (family-situation-exemption))
      0
      (+ (* a 0.19) (* b 0.24) (* c 0.3) (* d 0.37) (* e 0.45)))))

(defn calc
  []
  (let [{:keys [annual-gross installments contract]} @data
        s-social-contribution (s-social)
        irpf (irpf)
        annual-net (-> annual-gross
                       (- s-social-contribution irpf))]
    {:net-wage (/ annual-net installments) :s-social-contribution (/  s-social-contribution installments) :irpf (/ irpf installments)}))

