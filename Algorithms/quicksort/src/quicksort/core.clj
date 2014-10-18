(ns quicksort.core
  (:gen-class))

(defn quicksort
  "Implementation of the quicksort algorithm"
  [[pivot & coll]]
  (when pivot
    (concat (quicksort (filter #(< % pivot) coll)) ; Smaller
            [pivot]
            (quicksort (remove #(< % pivot) coll))))) ; Larger

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
