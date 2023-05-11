(ns main.auxiliares
  (:require [clojure.java.io :refer [make-parents]]))

; Define the products and currencies that the machines will operate with
(def productos
  ; Product Quantity Price
  '([A 10 9]
    [B 8 11]
    [C 6 15]
    [D 6 17]
    [E 5 25]
    [F 5 30]
    [G 5 42]
    [H 5 55]))

(def monedas
  ; Value Quantity Max
  '([1 25 50]
    [2 10 20]
    [5 10 20]
    [10 10 20]
    [20 5 10]
    [50 5 10]))


;; --- GENERATE TRANSACTION ---
;; Function that generates a transaction with random data
;; (current transaction) -> (transaction)

(defn generar-transaccion [actual] 
  [; Transaction ID
   actual 
   ; Get random product
   (nth (map first productos) (rand-int (count productos)))
   ; Get random coins
   (take (rand-int 6) (repeatedly #(nth (map first monedas) (rand-int (count monedas)))))])


;; --- GENERATE TRANSACTIONS ---
;; Function that generates n transactions with random values
;; (number of transactions, current machine, transactions, current transaction) -> (map with transactions)

(defn generar-transacciones [nTransacciones maquina transacciones actual]
  ; Return map of transactions
  (if (= nTransacciones actual) {:maquina maquina :transacciones transacciones}
      ; Add transaction and continue iterating
      (generar-transacciones nTransacciones maquina (concat transacciones (list (generar-transaccion actual))) (inc actual))))


;; --- GENERATE MACHINES ---
;; Function that generates the files for the machines
;; (number of machines, number of transactions per machine, current machine) -> nil

(defn generar-maquinas [nMaquinas nTransacciones actual]
  (if (= actual nMaquinas) nil
      (do
        ; Create parent directories
        (make-parents (str "data/" actual "/i.txt"))
        ; Create inventory file
        (spit (str "data/" actual "/i.txt") {:maquina actual :inv-productos productos :inv-monedas monedas})
        ; Create transaction file
        (spit (str "data/" actual "/t.txt") (generar-transacciones nTransacciones actual '() 0))
        ; Move on to create the next machine
        (generar-maquinas nMaquinas nTransacciones (inc actual)))))


;; --- PRINT RESULT ---
;; Function that prints the result of a transaction
;; (transaction result) -> nil

(defn print-resultado [res]
  ; If the state is 1, the transaction was successful
  (if (= (nth res 0) 1)
    (do (printf "%s. OK: %s | Product: %s | Price: %s | Entered: %s | Change: "
                (nth res 1) (nth res 2) (nth res 3) (nth res 4) (nth res 5)) (println (nth res 6)))
    ; If the state was 0, there was an error
    (printf "%s. ERROR: %s | Product: %s | Change returned: %s\n"
            (nth res 1) (nth res 2) (nth res 3) (nth res 6))))


;; --- PRINT MACHINE RESULTS ---
;; Function that prints the results of a machine
;; (machine number, earnings, results, alerts) -> nil
(defn imprimir-resultados-maquina [n-maquina ganancia resultados alertas-prod-min alertas-mon-min alertas-mon-max]
  (println "\n\n--------- MACHINE RESULTS" n-maquina "---------")
  (dorun (map print-resultado resultados))
  (println "Earnings: " ganancia)
  (println "Products with low inventory: " alertas-prod-min)
  (println "Coins with low inventory: " alertas-mon-min)
  (println "Coins with high inventory: " alertas-mon-max))


;; --- FIND TOP POSITION ---
;; Function that returns the new top by arranging the new machine in its position
;; (machine number, earnings, past elements, top-10) -> (new top-10)
(defn buscar-lugar [n-maquina ganancia pasados top-10]
  (if
   ; If the current machine is larger than the first and second of the top-10
   (and (> ganancia (second (first top-10))) (> ganancia (second (second top-10))))
    (if
     ; And if there were only two elements, it is because the current one is the largest machine and goes to the end 
     (= (count top-10) 2)
      ; Add machine to the end of the top
      (concat
       ; Remove the first and second element from the past elements and remove the first from pasados because it is the smallest
       (drop 1 (concat pasados (list (first top-10)) (list (second top-10))))
       ; Add the current machine that is now the largest
       (list (list n-maquina ganancia)))
      
      ; Move to the next iteration
      (buscar-lugar n-maquina ganancia (concat pasados (list (first top-10))) (rest top-10)))
   ; If not, it is because it is larger than the first but smaller than the second and this is its place
   (concat
    ; Remove the first element from the previous top because it is the smallest
    (drop 1 (concat pasados (list (first top-10))))
     ; Add the current machine
    (list (list n-maquina ganancia))
     ; The rest of the top
    (rest top-10))))


;; --- CHECK TOP 10 ---
;; Function that checks if a machine enters the top 10 of machines with the highest profit
;; (machine number, machine profit, current top 10)
(defn checar-top [n-maquina ganancia top-10]
  (if
   ; If there are less than 10 elements
   (< (count top-10) 10)
    ; Add the current machine where it goes
      (concat (filter (fn [par] (<= (second par) ganancia)) top-10)
              (list (list n-maquina ganancia))
              (filter (fn [par] (> (second par) ganancia)) top-10))
    ; If there are already 10 elements
      (if
     ; If the current profit is smaller than the smallest profit in the top, return the top as it was
       (<= ganancia (second (first top-10))) top-10
     ; If it's bigger, it should enter the top and we need to find its place
       (buscar-lugar n-maquina ganancia '() top-10))))

;; --- PRINT GENERAL RESULTS ---
(defn imprimir-resultados-generales [ganancia alertas-prod-min alertas-mon-min alertas-mon-max top-10]
  (println "\n\n\n")
  (println "--- GENERAL RESULTS ---")
  (println "\n- Total Profit:" ganancia)
  (println "\n- Top 10 machines with highest profit")
  (dorun (map (fn [par] (println "  Machine:" (first par)"| Profit:" (second par))) top-10))
  (println "\n- Machine IDs with alerts")
  (println "  Machines with low products: " alertas-prod-min)
  (println "  Machines with few coins: " alertas-mon-min)
  (println "  Machines with many coins: " alertas-mon-max)
  (println "\n\n\n"))

;; --- SHOW GENERAL RESULTS ---
;; Function that shows the general results of all the processed machines
;; (number of machines, current machine, total profit, top-10, alerts) -> (nil)
(defn resultados-generales [n-maquinas actual ganancia top-10 alertas-prod-min alertas-mon-min alertas-mon-max]
  (if (= n-maquinas actual) (imprimir-resultados-generales ganancia alertas-prod-min alertas-mon-min alertas-mon-max top-10)
      ; Read results from current machine
      (let [res (read-string (slurp (str "data/" actual "/r.txt")))]
        ; Print results from current machine
        (imprimir-resultados-maquina actual (get res :ganancia) (get res :resultados)
                                     (get res :alertas-prod-min) (get res :alertas-mon-min)
                                     (get res :alertas-mon-max))
        ; Move to the next machine
        (resultados-generales n-maquinas
                              ; Move to next machine
                              (inc actual)
                              ; Add current profit
                              (+ ganancia (get res :ganancia))
                              ; Check if it's in the top 10
                              (checar-top actual (get res :ganancia) top-10)
                              ; If there are alerts for low product, add the machine
                              (if (= (get res :alertas-prod-min) nil) alertas-prod-min (concat alertas-prod-min (list actual)))
                              ; If there are alerts for few coins, add the machine
                              (if (= (get res :alertas-mon-min) nil) alertas-mon-min (concat alertas-mon-min (list actual)))
                              ; If there are alerts for many coins, add the machine
                              (if (= (get res :alertas-mon-max) nil) alertas-mon-max (concat alertas-mon-max (list actual)))))))
