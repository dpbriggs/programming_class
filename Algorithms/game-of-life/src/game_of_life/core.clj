(ns game-of-life.core
  (:gen-class))

(defrecord Cell [x y])

(defn make-cells
  "Takes a seq of points and makes all of them into a Cell"
  [cells]
  (set (map #(apply ->Cell %) cells)))

(defn make-game-map
  "Makes game map of Cells"
  [height width]
  (set (for [x (range height) y (range width)]
         (->Cell x y))))

(defn get-neighbours
  "Given a specific cell return a list of neighbours"
  [cell]
  (let [cell-x (:x cell)
        cell-y (:y cell)
        around-x [(- cell-x 1) cell-x (+ cell-x 1)]
        around-y [(- cell-y 1) cell-y (+ cell-y 1)]]
    (for [x around-x y around-y
          :when (not (and (= cell-x x) (= cell-y y)))]
      [x y])))

(defn live-neighbours
  "Returns number of live neighbours"
  [world cell]
  (let [neighbours (get-neighbours cell)]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
