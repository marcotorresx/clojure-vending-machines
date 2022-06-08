(ns evidencia2.auxiliares
  (:require [clojure.java.io :refer [make-parents]]))

; Definir los productos y monedas con que van a operar las máquinas
(def productos
  ; Producto Cantidad Precio
  '([A 10 5]
    [B 8 9]
    [C 6 13]
    [D 6 15]
    [E 5 18]
    [F 5 25]
    [G 5 30]
    [H 5 44]))

(def monedas
  ; Valor Cantidad Max
  '([1 30 40]
    [2 10 20]
    [5 2 20]
    [10 0 20]
    [20 0 10]
    [50 0 10]))


;; --- GENERAR TRANSACCIÓN ---
;; Función que genera una transacción con datos random
;; (transacción actual) -> (transacción)
(defn generar-transaccion [actual] 
  [; Id de transacción
   actual 
   ; Obtener producto random
   (nth (map first productos) (rand-int (count productos)))
   ; Obtener monedas random
   (take (rand-int 6) (repeatedly #(nth (map first monedas) (rand-int (count monedas)))))
   ])


;; --- GENERAR TRANSACCIONES ---
;; Función que genera transacciones con valores random
;; (cantidad de transacciones, máquina actual, transacciones, transacción actual) -> (mapa con transacciones)

(defn generar-transacciones [nTransacciones maquina transacciones actual]
  ; Regresar mapa de transacciones
  (if (= nTransacciones actual) {:maquina maquina :transacciones transacciones}
      ; Añade transacción y continua iterando
      (generar-transacciones nTransacciones maquina (concat transacciones (list (generar-transaccion actual))) (inc actual))))


;; --- GENERAR MÁQUINAS ---
;; Función que llama a la función para generar los archivos de las máquinas
;; (cantidad de máquinas, cantidad de transacciones por máquina) -> nil

(defn generar-maquinas [nMaquinas nTransacciones actual]
  (if (= actual nMaquinas) nil
      (do
        ; Crear directorios padre
        (make-parents (str "data/" actual "/i.txt"))
        ; Crear archivo de inventarios
        (spit (str "data/" actual "/i.txt") {:maquina actual :inv-productos productos :inv-monedas monedas})
        ; Crear archivo de transacciones
        (spit (str "data/" actual "/t.txt") (generar-transacciones nTransacciones actual '() 0))
        ; Pasar a crear la siguiente máquina
        (generar-maquinas nMaquinas nTransacciones (inc actual)))))


;; --- PRINT RESULTADO ---
;; Función que imprime el resultado de una transacción
;; (estado de transacción, id transacción, mensaje, producto, precio, cant ingresada, monedas) -> (void)

(defn print-resultado [res]
  ; Si el estado es 1 la transacción fue exitosa
  (if (= (nth res 0) 1)
    (do (printf "%s. OK: %s | Producto: %s | Precio: %s | Ingresado: %s | Cambio: "
                (nth res 1) (nth res 2) (nth res 3) (nth res 4) (nth res 5)) (println (nth res 6)))
    ; Si el estado fue 0 hubo un error
    (printf "%s. ERROR: %s | Producto: %s | Se regresa: %s\n"
            (nth res 1) (nth res 2) (nth res 3) (nth res 6))))


;; --- IMPRIMIR RESULTADOS DE MÁQUINA ---
(defn imprimir-resultados-maquina [n-maquina ganancia resultados alertas-prod-min alertas-mon-min alertas-mon-max]
  (println "\n\n--------- RESULTADOS MÁQUINA" n-maquina "---------")
  (dorun (map print-resultado resultados))
  (println "Ganancia: " ganancia)
  (println "Productos con poco inventario: " alertas-prod-min)
  (println "Monedas con poco inventario: " alertas-mon-min)
  (println "Monedas con mucho inventario: " alertas-mon-max))


;; --- MOSTRAR RESULTADOS GENERALES ---
(defn resultados-generales [n-maquinas actual ganancia alertas-prod-min alertas-mon-min alertas-mon-max]
  (if (= n-maquinas actual) (list ganancia alertas-prod-min alertas-mon-min alertas-mon-max)
      ; Leer resultados de máquina acutal
      (let [res (read-string (slurp (str "data/" actual "/r.txt")))]
        ; Imprimir resultados de la máquina actual
        (imprimir-resultados-maquina actual (get res :ganancia) (get res :resultados)
                                     (get res :alertas-prod-min) (get res :alertas-mon-min)
                                     (get res :alertas-mon-max))
        ; Checar si es top 10
        ; Pasar a la siguiente máquina
        (resultados-generales n-maquinas 
                              ; Pasar a siguiente máquina
                              (inc actual) 
                              ; Sumar la ganancia actual
                              (+ ganancia (get res :ganancia))
                              ; Si hay alertas de poco producto añadir máquina
                              (if (= (get res :alertas-prod-min) nil) alertas-prod-min (concat alertas-prod-min (list actual)))
                              ; Si hay alertas de pocas monedas añadir máquina
                              (if (= (get res :alertas-mon-min) nil) alertas-mon-min (concat alertas-mon-min (list actual)))
                              ; Si hay alertas de muchas monedas añadir máquina
                              (if (= (get res :alertas-mon-max) nil) alertas-mon-max (concat alertas-mon-max (list actual)))))))

(resultados-generales 3 0 0 '() '() '())

