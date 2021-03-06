(ns more-efficient-sieve-of-eratosthenes.core
  (:gen-class))

(defn gen-table
  "Returns a transient vector of values"
  [n]
  (transient (vec (range 3 n 2))))

(defn get-multiples-indices
  "Returns seq of indexes that correspond to multiples of n"
  [start end n]
  (for [y (range 1 end)
	:while (<= (+ start (* y n)) end)]
    (+ start (* y n))))

(defn mark-multiples
  "Marks multiples of starting point by replacing value with 0"
  [coll start]
  (map #(assoc! coll % 0)
       (get-multiples-indices start (count coll) (get coll start))))

(defn find-next-non-zero
  "Finds next non-zero number aka a new prime"
  [coll start]
  (loop [stop (inc start)]
    (if (not= 0 (get coll stop))
      stop
      (recur (inc stop)))))

(defn sieve-e
  "Implementation of sieve of eratosthenes"
  [n]
  (loop [coll (gen-table n)
	 start 0]
    (if (empty? (mark-multiples coll start)) ;; No more multiples to cross out
      (disj (apply sorted-set (into [2] (persistent! coll))) 0)
      (recur coll (find-next-non-zero coll start)))))

(defn write-to-file
  "Writes collection of primes to a file called primes.txt"
  [primes]
  (let [new-line (System/getProperty "line.separator")
	info (clojure.string/join new-line primes)
	home (System/getProperty "user.home")]
    (spit (str home "/primes.txt") info)))

(defn -main
  "Computes primes and spits them to file"
  ([n]
     (write-to-file (sieve-e (Integer. n))))
  ([]
     (do
       (time (write-to-file (sieve-e 1000000)))
       (read-line))))
