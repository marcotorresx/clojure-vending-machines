(ns evidencia2.transacciones)


;; --- MODIFICAR INVENTARIO  ---
;; Función que modifica la cantidad de inventario dependiendo del operador
;; (inventario, inv-anterior, elemento, operador) -> (nuevo inventario)

(defn modificar-inventario [inventario inv-anterior elemento operador]
  (cond
    ; Si se encuentran los datos del elemento buscado
    (= elemento (get (first inventario) 0))
     ; Regresa una nueva lista que junta
    (concat
      ; Los datos de los elementos anteriores
     inv-anterior
      ; Los datos del elemento buscado aplicando el operador a la cantidad en inventario
     (list [(get (first inventario) 0)
            (operador (get (first inventario) 1) 1)
            (get (first inventario) 2)])
      ; Los datos de los elementos que seguían
     (rest inventario))

    ; Llama recursivamente a la función pasando
    :else (modificar-inventario
           ; El resto de los elementos
           (rest inventario)
           ; Añande los datos actuales a la lista de datos anteriores
           (concat inv-anterior (list (first inventario)))
           elemento operador)))


;;  --- MODIFICAR-INVENTARIO? ---
;; Función que regresa si es válida la modificación del inventario de una moneda dependiendo del operador
;; (inv-monedas, moneda, operador) -> (1 o estado de salida)

(defn modificar-inventario? [inv-monedas moneda operador]
  (cond
    ; Si se encuentra la moneda
    (= moneda (get (first inv-monedas) 0))
    (cond
      ; Si el operador es - y la moneda tiene de inventario 0, regresa estado de salida -1
      (and (= operador -) (<= (get (first inv-monedas) 1) 0)) -1
      ; Si el operador es + y la cantidad es igual a la cantidad máxima, regresa estado de salida -2
      (and (= operador +) (>= (get (first inv-monedas) 1) (get (first inv-monedas) 2))) -2
      ; Si es válida la modificación, regresa 1
      :else 1)

    ; Seguir buscando
    :else (modificar-inventario? (rest inv-monedas) moneda operador)))


;; --- BUSCAR PRODUCTO ---
;; Función que busca el producto de la transacción
;; (producto, inv-productos) -> ( '(precio, nuevo inv-productos) ó estado de salida)

(defn buscar-producto [producto inv-productos todos-productos]
  (cond
    ; Si no se encontró regresa estado de salida -1
    (empty? inv-productos) -1
    ; Si se encuentra el producto
    (= producto (get (first inv-productos) 0))
    ; Checar que haya en inventario
    (cond
      ; Si no hay en inventario regresa estado de salida -2
      (<= (get (first inv-productos) 1) 0) -2

      ; Si hay en inventario regresa lista con el precio
      :else (list (get (first inv-productos) 2)
                  ; Y con el nuevo inv-productos
                  (modificar-inventario todos-productos '() producto -)))

    ; Seguir buscando
    :else (buscar-producto producto (rest inv-productos) todos-productos)))


;; --- AGREGAR MONEDAS ---
;; Función que regresa el nuevo inventario de monedas con las monedas agregadas o estado de salida
;; (inv-monedas, monedas ingresadas) -> (nuevo inv-monedas o estado de salida)

(defn agregar-monedas [inv-monedas monedas]
  (cond
    ; Si ya no hay monedas que agregar regresa el nuevo inventario
    (empty? monedas) inv-monedas
    ; Si no se puede agregar moneda por inventario lleno, regresa estado de salida -1
    (< (modificar-inventario? inv-monedas (first monedas) +) 0) -1

    ; Llama recursivamente a la función
    :else (agregar-monedas
           ; Pasando el nuevo inventario con la cantidad de la moneda incrementada
           (modificar-inventario inv-monedas '() (first monedas) +)
           ; Analizar siguiente moneda
           (rest monedas))))


;; --- CALCULA CAMBIO ---
;; Función que calcula el cambio de la transacción y genera el nuevo inventario de monedas
;; (cantidad cambio, monedas de cambio, inv-monedas, valores) -> ( '(cambio, nuevo inv-monedas) )

(defn calcula-cambio [cant-cambio m-cambio inv-monedas valores]
  (cond
    ; Si la cantidad de cambio es 0, regresa una lista con las monedas de cambio y el nuevo inventario
    (<= cant-cambio 0) (list m-cambio inv-monedas)
    ; Si se han intentado todos los valores no hay cambio suficiente, regresa estado de salida -3
    (empty? valores) -3

    :else
    (cond
       ; Si el valor actual es más chico que la cantidad de cambio y su inventario se puede restar 1
      (and (<= (first valores) cant-cambio) (> (modificar-inventario? inv-monedas (first valores) -) 0))

       ; Llama recursivamente a la función
      (calcula-cambio
         ; La nueva cantidad de cambio es la resta entre la cantidad actual y el valor actual
       (- cant-cambio (first valores))
         ; Se agrega la moneda actual a las monedas de cambio
       (concat m-cambio (list (first valores)))
         ; Se genera nuevo inventario restando en 1 la cantidad
       (modificar-inventario inv-monedas '() (first valores) -)
         ; Se mantienen los valores para volver a checar el valor actual
       valores)

       ; Si el valor no puede ser cambio intenta con el siguiente valor y sin cambiar los parámetros
      :else (calcula-cambio cant-cambio m-cambio inv-monedas (rest valores)))))


;; --- VALIDAR MONEDAS INGRESADAS ---
;; Función que valida las monedas ingresadas basandonos en un autómata y llama a calcula-cambio o regresa estado de salida
;; (cantidad ingresada, monedas ingresadas, precio, inv-monedas) -> ('(cambio, nuevo-inv-monedas) )

(defn validar-monedas [cant-ingresada m-ingresadas precio inv-monedas]
  (cond
    ; Si se acabaron las monedas
    (empty? m-ingresadas)
    (if
     ; Si el estado final no es estado aceptor (no es mayor que el precio) regresa estado de salida -1
     (< cant-ingresada precio) -1

    ; Si esta bien, llama a calcula-cambio y regresa el cambio y nuevo inventario o estados de salida
     (calcula-cambio
      ; La cantidad de cambio es la resta entre el dinero ingresado y el precio
      (- cant-ingresada precio) '() inv-monedas
      ; Pasar lista de valores de monedas en orden decreciente
      (reverse (map first inv-monedas))))

    ; Iterar los valores y verificar que la moneda sea válida o regresar estado de salida -2
    (<= (apply + (map (fn [valor] (if (= valor (first m-ingresadas)) 1 0)) (map first inv-monedas))) 0) -2

    ; Llamar recursivamente a la función con la suma de la cantidad con la moneda
    :else (validar-monedas (+ cant-ingresada (first m-ingresadas)) (rest m-ingresadas) precio inv-monedas)))


;; --- PROCESA TRANSACCIÓN ---
;; Función que procesa una transacción y devuelve ganancia o imprime errores
;; (id máquina, transaccion, inv-productos, inv-monedas, resultado de precio, resultado de cambio, bool monedas agregadas) -> (ganancia de transaccion)

(defn procesa-transaccion [id-maquina transaccion inv-productos inv-monedas res-precio res-cambio m-agregadas]
  (cond

    ; --- VERIFICAR PRECIO Y PRODUCTO ---
    ; Si no hay res-precio es porque aún no se ha buscado el producto
    (nil? res-precio)
     ; Llamar a la función reecursivamente
    (procesa-transaccion id-maquina transaccion inv-productos inv-monedas
                          ; Pasando como parametro de res-precio el resultado de buscar-producto
                         (buscar-producto (get transaccion 1)
                                           ; Se pasa un inventario para iterar sobre él
                                          inv-productos
                                           ; Se pasa otro inventario para restar la cantidad
                                          inv-productos)
                         res-cambio m-agregadas)
    ; --- VALIDACIONES ---
    ; Si el res-precio es -1 es porque no se encontró el producto, marca error y regresa ganancia 0
    (= res-precio -1)
    (list 0 (list 0 (get transaccion 0) "No se encontró producto"
                (get transaccion 1) 0 0 (get transaccion 2)))
    ; Si el res-precio es -2 es porque no hay inventario, marca error y regresa ganancia 0
    (= res-precio -2)
    (list 0 (list 0 (get transaccion 0) "No hay inventario de producto"
                (get transaccion 1) 0 0 (get transaccion 2)))

    ; --- VERIFICAR MONEDAS Y CALCULAR CAMBIO ---
    ; Si no hay res-cambio es porque aún no se han procesado las monedas y el cambio
    (nil? res-cambio)
     ; Llamar a la función recursivamente
    (procesa-transaccion id-maquina transaccion inv-productos inv-monedas res-precio
                          ; Pasando como parametro de res-cambio el resultado de validar-monedas
                         (validar-monedas
                          0
                          ; Monedas ingresadas
                          (get transaccion 2)
                          ; Pasar el precio de producto
                          (first res-precio) inv-monedas)
                         m-agregadas)
    ; --- VALIDACIONES ---
    ; Si el res-cambio es -1 es porque no se alcanzó el precio del producto, regresa ganancia 0
    (= res-cambio -1)
    (list 0 (list 0 (get transaccion 0) "Dinero no suficiente"
              (get transaccion 1) 0 0 (get transaccion 2)))
    ; Si el res-cambio es -1 es porque se ingresó una moneda inválida, regresa ganancia 0
    (= res-cambio -2)
    (list 0 (list 0 (get transaccion 0) "Se ingresó una moneda no válida"
              (get transaccion 1) 0 0 (get transaccion 2)))
    ; Si el res-cambio es -3 es porque no hubo cambio suficiente, regresa ganancia 0
    (= res-cambio -3)
    (list 0 (list 0 (get transaccion 0) "No es posible entregar cambio"
              (get transaccion 1) 0 0 (get transaccion 2)))


    ; --- AGREGAR MONEDAS INGRESADAS A INVENTARIO ---
    ; Si no hay m-agregadas es porque aún no se han agregado las monedas
    (nil? m-agregadas)
     ; Llamar a la función recursivamente
    (procesa-transaccion id-maquina transaccion inv-productos
                          ; Genera nuevo inventario usando el inventario resultado del cambio
                         (agregar-monedas (second res-cambio) (get transaccion 2))
                          ; Cambiar monedas agregadas a 1 para indicar operación realizada
                         res-precio res-cambio 1)
    ; --- VALIDACIONES ---
    ; Si el inv-monedas es -1 es porque no hubo espacio suficiente, regresa ganancia 0
    (= inv-monedas -1)
    (list 0 (list 0 (get transaccion 0) "No hay suficiente espacio en inventario"
                               (get transaccion 1) 0 0 (get transaccion 2)))
    
    ; Si todo es correcto
    :else (do
            ; Actualiza el archivo de inventarios de la máquina
            (spit (str "data/" id-maquina "/i.txt") 
                  {:maquina id-maquina :inv-productos (second res-precio) :inv-monedas inv-monedas})
            ; Imprime venta exitosa
            (list (first res-precio) (list 1 (get transaccion 0) "Venta exitosa"
                  (get transaccion 1) (first res-precio) (apply + (get transaccion 2)) (first res-cambio))))))


;; --- ALERTA INVENTARIO ---
;; Función que genera lista de elementos en alerta
;; (tipo de inventario, inventario, <= ó >, + ó -, margen) -> (elementos en alerta)

(defn alerta-inventario [inventario operador-1 operador-2 margen]
  (cond
    ; Si ya se recorrieron todos los elementos regresa lista vacía
    (empty? inventario) nil
    ; Si hay alerta
    (operador-1 (get (first inventario) 1) (operador-2 (if (= operador-1 <=) 0 (get (first inventario) 2)) margen))
    ; Añade el elemento a la lista
    (cons (get (first inventario) 0) (alerta-inventario (rest inventario) operador-1 operador-2 margen))

    ; Si no hay alerta
    :else (alerta-inventario (rest inventario) operador-1 operador-2 margen)))


;; --- PROCESAR-MÁQUINA ---
;; Función que itera las transacciones y llama a la función procesa-transaccion
;; (transacciones) -> (nil)
;; (procesa-transaccion (first transacciones) (get inventarios :inv-productos) (get inventarios :inv-monedas) nil nil nil)

(defn procesar-maquina [id-maquina transacciones ganancia-total resultados]
  ; Si ya no hay transacciones
  (if (empty? transacciones)
    
    ; Abrir los inventarios
    (let [inventarios (read-string (slurp (str "data/" id-maquina "/i.txt")))]
      ; Guardar los resultados del procesamiento de máquina en un archivo
      (spit (str "data/" id-maquina "/r.txt")
            {:maquina id-maquina :ganancia ganancia-total :resultados resultados
             ; Obtener las alertas de inventario
             :alertas-prod-min (alerta-inventario (get inventarios :inv-productos) <= + 3)
             :alertas-mon-min (alerta-inventario (get inventarios :inv-monedas) <= + 2)
             :alertas-mon-max (alerta-inventario (get inventarios :inv-monedas) > - 2)}))
    
    ; Obtener los inventarios de la máquina actualizados
    (let [inventarios (read-string (slurp (str "data/" id-maquina "/i.txt")))]
      ; Procesar la transacción y obtener el resultado que es la ganancia y el string resultado
      (let [resultado (procesa-transaccion id-maquina (first transacciones)
                                           (get inventarios :inv-productos) (get inventarios :inv-monedas) nil nil nil)]
        
        ; Pasar a la siguiente transacción
        (procesar-maquina id-maquina (rest transacciones)
                              ; Sumando la ganancia de esta transacción
                              (+ ganancia-total (first resultado))
                              ; Agregando el resultado de esta transacción
                              (concat resultados (list (second resultado))))))))