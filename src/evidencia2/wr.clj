(def productos
  '([A 10 5]
    [B 8 9]
    [C 6 13]
    [D 6 15]
    [E 5 18]
    [F 5 25]
    [G 5 30]
    [H 5 44]))

(def monedas
  '([1 30 40]
    [2 10 20]
    [5 2 20]
    [10 0 20]
    [20 0 10]
    [50 0 10]))

(def transacciones
  '([1 G (50 50)]
    [2 G (10 10 1 1 1 1 1 1 1 1 1 1)]
    [3 G (10 10 1 1 1 1 1 1 1 1 1 1)]))

(def id-maquina 2)

; Inventarios
(spit (str "data/" id-maquina "/i.txt") 
                  {:maquina id-maquina :inv-productos productos :inv-monedas monedas})

; Transacciones
(spit (str "data/" id-maquina "/t.txt") 
                  {:maquina id-maquina :transacciones transacciones})

;; Leer resultados
(def resultados (read-string (slurp (str "data/" 2 "/r.txt"))))
(println "ganancia: " (get resultados :ganancia))
(println "resultados: " (get resultados :resultados))
(println "alertas-prod-min: " (get resultados :alertas-prod-min))
(println "alertas-mon-min: " (get resultados :alertas-mon-min))
(println "alertas-mon-max: " (get resultados :alertas-mon-max))

;; Leer inventarios
(def inventarios (read-string (slurp (str "data/" 2 "/i.txt"))))
(println "productos: " (get inventarios :inv-productos))
(println "monedas: " (get inventarios :inv-monedas))