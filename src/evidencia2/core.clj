(ns evidencia2.core
  (:gen-class)
  (:require [evidencia2.auxiliares :as aux]
            [evidencia2.transacciones :as trans]))
(defn -main [])

;; --- PROCESAR MÁQUINAS ---
;; Función que toma una lista de máquinas y las particiona
;; 


;; --- EJECUCIÓN ---

; 1. Generar los inventarios y transacciones de cada máquina
(def n-maquinas 3)
;; (def n-transacciones 5)
;; (aux/generar-maquinas nMaquinas nTransacciones 0)

; 2. Procesar las transacciones de cada máquina
(pmap
 ; Para cada numero de máquina
 (fn [n-maquina]
   ; Procesamos la máquina pasando las transacciones leídas de su archivo
   (trans/procesar-maquina n-maquina (get (read-string (slurp (str "data/" n-maquina "/t.txt"))) :transacciones) 0 '()))
 (range n-maquinas))

; 3. Mostrar los resultados de todas las cajas
