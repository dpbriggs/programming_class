(ns dijkstras-algorithm.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def inf (Integer/MAX_VALUE))

(def graph {:a {:b 5 :c 4}
            :b {:a 5 :d 3}
            :c {:a 4 :d 7}
            :d {:b 3 :c 7}})
