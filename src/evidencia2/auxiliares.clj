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
;; Función que genera n transacciones con valores random
;; (cantidad de transacciones, máquina actual, transacciones, transacción actual) -> (mapa con transacciones)

(defn generar-transacciones [nTransacciones maquina transacciones actual]
  ; Regresar mapa de transacciones
  (if (= nTransacciones actual) {:maquina maquina :transacciones transacciones}
      ; Añade transacción y continua iterando
      (generar-transacciones nTransacciones maquina (concat transacciones (list (generar-transaccion actual))) (inc actual))))


;; --- GENERAR MÁQUINAS ---
;; Función que generar los archivos de las máquinas
;; (cantidad de máquinas, cantidad de transacciones por máquina, máquina actual) -> nil

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
;; (resultado de transacción) -> nil

(defn print-resultado [res]
  ; Si el estado es 1 la transacción fue exitosa
  (if (= (nth res 0) 1)
    (do (printf "%s. OK: %s | Producto: %s | Precio: %s | Ingresado: %s | Cambio: "
                (nth res 1) (nth res 2) (nth res 3) (nth res 4) (nth res 5)) (println (nth res 6)))
    ; Si el estado fue 0 hubo un error
    (printf "%s. ERROR: %s | Producto: %s | Se regresa: %s\n"
            (nth res 1) (nth res 2) (nth res 3) (nth res 6))))


;; --- IMPRIMIR RESULTADOS DE MÁQUINA ---
;; Función que imprime los resultados de una máquina
;; (numero de máquina, ganancia, resultados, alertas) -> nil

(defn imprimir-resultados-maquina [n-maquina ganancia resultados alertas-prod-min alertas-mon-min alertas-mon-max]
  (println "\n\n--------- RESULTADOS MÁQUINA" n-maquina "---------")
  (dorun (map print-resultado resultados))
  (println "Ganancia: " ganancia)
  (println "Productos con poco inventario: " alertas-prod-min)
  (println "Monedas con poco inventario: " alertas-mon-min)
  (println "Monedas con mucho inventario: " alertas-mon-max))


;; --- BUSCAR LUGAR DEL TOP ---
;; Función que regresa el nuevo top acomodando la nueva máquina en su lugar
(defn buscar-lugar [n-maquina ganancia pasados top-10]
  (println pasados)
  (if
   ; Si la máquina actual es más grande que el first y second del top-10
   (and (> ganancia (second (first top-10))) (> ganancia (second (second top-10))))
    (if
     ; Y si solo habían dos elementos es porque la actual es la máquina más grande y va al final 
     (= (count top-10) 2)
      ; Agregar máquina al final del top
      (concat
       ; Quitar el primer y segundo elemento a los pasados y quitar el primero de pasados porque es el más pequeño
       (drop 1 (concat pasados (list (first top-10)) (list (second top-10))))
       ; Añadir la máquina actual que es ahora la más grande
       (list (list n-maquina ganancia)))
      ; Pasar a la siguiente iteración
      (buscar-lugar n-maquina ganancia (concat pasados (list (first top-10))) (rest top-10)))

   ; Si no es porque es más grande que el primero pero más chico que el segundo y este es su lugar
   (concat
    ; Quitar el primer elemento del top anterior porque es el más pequeño
    (drop 1 (concat pasados (list (first top-10))))
     ; Añadir la máquina actual
    (list (list n-maquina ganancia))
     ; El resto del top
    (rest top-10))))


;; --- CHECAR TOP 10 ---
;; Función que checa si una máquina entra en el top 10 de máquinas con mayor ganancia
;; (número de máquina, ganancia de la máquina, actual top 10)

(defn checar-top [n-maquina ganancia top-10]
  (if
   ; Si todavía no hay 10 elementos dentro
   (< (count top-10) 10)
    ; Agrega la máquina actual en donde va
    (concat (filter (fn [par] (<= (second par) ganancia)) top-10) 
            (list (list n-maquina ganancia)) 
            (filter (fn [par] (> (second par) ganancia)) top-10))

    ; Si ya hay 10 elementos
    (if
     ; Si la ganancia actual es más chica que la ganancia más pequeña del top regresa el top como estaba
     (<= ganancia (second (first top-10))) top-10
     ; Si es más grande significa que debe de entrar en el top y hay que buscar su lugar
     (buscar-lugar n-maquina ganancia '() top-10))))


;; --- MOSTRAR RESULTADOS GENERALES ---
;; Función que muestra los resultados generales de todas las máquinas procesadas
;; (numero de maquinas, máquina actual, ganancia total, alertas) -> nil

(defn resultados-generales [n-maquinas actual ganancia top-10 alertas-prod-min alertas-mon-min alertas-mon-max]
  (if (= n-maquinas actual) (list ganancia alertas-prod-min alertas-mon-min alertas-mon-max top-10)
      ; Leer resultados de máquina acutal
      (let [res (read-string (slurp (str "data/" actual "/r.txt")))]
        ; Imprimir resultados de la máquina actual
        (imprimir-resultados-maquina actual (get res :ganancia) (get res :resultados)
                                     (get res :alertas-prod-min) (get res :alertas-mon-min)
                                     (get res :alertas-mon-max))
        ; Pasar a la siguiente máquina
        (resultados-generales n-maquinas
                              ; Pasar a siguiente máquina
                              (inc actual)
                              ; Sumar la ganancia actual
                              (+ ganancia (get res :ganancia))
                              ; Checar si es top 10
                              (checar-top actual (get res :ganancia) top-10)
                              ; Si hay alertas de poco producto añadir máquina
                              (if (= (get res :alertas-prod-min) nil) alertas-prod-min (concat alertas-prod-min (list actual)))
                              ; Si hay alertas de pocas monedas añadir máquina
                              (if (= (get res :alertas-mon-min) nil) alertas-mon-min (concat alertas-mon-min (list actual)))
                              ; Si hay alertas de muchas monedas añadir máquina
                              (if (= (get res :alertas-mon-max) nil) alertas-mon-max (concat alertas-mon-max (list actual)))))))

(resultados-generales 3 0 0 '() '() '() '())

