(ns bubble-sort.core
  (:gen-class))

(defn test-case
  "Generates a random test-case"
  []
  (shuffle (range 100)))

(defn swap
  "Swaps two values at specified indices"
  [coll i1 i2]
  (let [n1 (get coll i1)
	n2 (get coll i2)]
    (assoc! coll i1 n2)
    (assoc! coll i2 n1)))

(defn bubble-sort
  [x]
  (let [done (atom false)
	coll (transient x)]
    (while (not @done)
      (println "made it here")
      (swap! done (fn [n] true))
      (println "a")
      (for [i (range 1 (count coll))
	    :let [x1 (get coll (dec i))
		  x2 (get coll i)]
	    :when (> x1 x2)]
	  (do
	    (println "made it here1")
	    (swap! done (fn [n] false))
	    (assoc! coll (dec i) 99)
	    (assoc! coll i x1))))
    (persistent! coll)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
