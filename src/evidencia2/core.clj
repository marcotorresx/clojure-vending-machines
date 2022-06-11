(ns evidencia2.core
  (:gen-class)
  (:require [evidencia2.auxiliares :as aux]
            [evidencia2.transacciones :as trans]))
(defn -main [])


;; --- EJECUCIÓN ---

; 1. Generar los inventarios y transacciones de cada máquina
(println "\n------- SIMULADOR DE MAQUINAS EXPENDEDORAS -------")
(do (print "\nIngresa cantidad de maquinas: ") 
    (flush) 
    (def n-maquinas (Integer/parseInt (read-line))))
(do (print "Ingresa cantidad de transacciones por maquina: ") 
    (flush) 
    (def n-transacciones (Integer/parseInt (read-line))))
(aux/generar-maquinas n-maquinas n-transacciones 0)

; 2. Procesar las transacciones de cada máquina
(time (dorun (pmap 
        ; Función que recibe una sublista de máquinas
        (fn [sublista] (dorun (map 
                               ; A cada máquina de la sublista pasarla por una función
                               (fn [n-maquina] 
                                 ; Procesar la máquina actual
                                 (trans/procesar-maquina 
                                  n-maquina 
                                  (get (read-string (slurp (str "data/" n-maquina "/t.txt"))) :transacciones) 
                                  0 '())) 
                                   sublista)))
        ; Lista de máquinas particionada
        (partition-all 3 (range n-maquinas)))))


; 3. Mostrar los resultados de todas las máquinas
(aux/resultados-generales n-maquinas 0 0 '() '() '() '())
