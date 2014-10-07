(ns dijkstras-algorithm.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def inf (Math/pow (Integer/MAX_VALUE) 2))

(def graph {:a {:b 5 :c 4}
	    :b {:a 5 :d 3}
	    :c {:a 4 :d 7}
	    :d {:b 3 :c 7}
	    :e {:a 2 :b 3 :c 2 :d 4}})

(defn dijkstras-algorithm
  [graph source]
  (let []))
