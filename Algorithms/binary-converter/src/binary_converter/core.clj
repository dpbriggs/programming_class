(ns binary-converter.core
  (:gen-class))



(defn max-power
  "Returns the minimum power of two needed for binary conversion"
  [n]
  (+ 1 (Math/floor (/ (Math/log n) (Math/log 2)))))

(defn binary-converter
  "Repeatedly subtracts powers of two to convert to binary"
  [n]
  (loop [i (reverse (map #(Math/pow 2 %) (range (max-power n))))
         left-over n
         bin ""]
    (if (empty? i)
      (if (= n 0)
        "0"
        bin)
      (let [n-i (rest i)
            n-left-over (- left-over (first i))
            bin-end (if (<= 0 n-left-over) "1" "0")
            n-bin (str bin bin-end)]
        (if (> 0 n-left-over)
          (recur n-i left-over n-bin)
          (recur n-i n-left-over n-bin))))))

(defn dis-fact
  "Breaks factorial function into several seperate threads
   for better performance"
  [n]
  (reduce *' (pmap #(reduce *' %)
                   (partition-all
                    (* 3 (Math/floor (Math/log n)))
                    (range 1 (+ n 1))))))

(defn logBigInteger
  "Adapted from java implementation from here:
   http://stackoverflow.com/a/7982137"
  [n-BigInt]
  (let [n (biginteger n-BigInt)
        blex (- (.bitLength n) 1022)
        val (if (> blex 0) (.shiftRight n blex) n)
        res (Math/log (.doubleValue val))]
    (if (> blex 0)
      (+ res (* blex (Math/log 2)))
      res)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
