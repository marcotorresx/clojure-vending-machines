(ns evidencia2.core
  (:gen-class)
  (:require [evidencia2.auxiliares :as aux]
            [evidencia2.transacciones :as trans]))
(defn -main [])


;; --- EXECUTION ---

; 1. Generate inventories and transactions for each machine
(println "\n------- VENDING MACHINE SIMULATOR -------")
(do (print "\nEnter the number of machines: ") 
    (flush) 
    (def n-maquinas (Integer/parseInt (read-line))))
(do (print "Enter the number of transactions per machine: ") 
    (flush) 
    (def n-transacciones (Integer/parseInt (read-line))))
(aux/generar-maquinas n-maquinas n-transacciones 0)

; 2. Process the transactions for each machine
(time (dorun (pmap 
        ; Function that receives a sublist of machines
        (fn [sublista] (dorun (map 
                               ; Apply a function to each machine in the sublist
                               (fn [n-maquina] 
                                  ; Process the current machine
                                 (trans/procesar-maquina 
                                  n-maquina 
                                  (get (read-string (slurp (str "data/" n-maquina "/t.txt"))) :transacciones) 
                                  0 '())) 
                                   sublista)))
        ; Partitioned list of machines
        (partition-all 3 (range n-maquinas)))))


; 3. Display the results for all machines
(aux/resultados-generales n-maquinas 0 0 '() '() '() '())
