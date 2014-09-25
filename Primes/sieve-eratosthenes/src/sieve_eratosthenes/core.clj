(ns sieve-eratosthenes.core
  (:gen-class))

(defn filter-multiples
  [n nums]
  (remove #(zero? (mod % n)) nums))

(defn sieve-eratosthenes
  [up-to]
  (loop [nums       (range 2 up-to)
         head-prime (first nums)
         primes     [head-prime]]
    (if (empty? nums)
        primes
        (let [new-nums       (filter-multiples head-prime nums)
              new-head-prime (first new-nums)
              new-primes     (conj primes new-head-prime)]
          (recur new-nums new-head-prime new-primes)))))

(defn write-to-file
  [primes]
  (let [new-line (System/getProperty "line.separator")
        info (clojure.string/join new-line primes)
        home (System/getProperty "user.home")]
    (spit (str home "/primes.txt") info)))

(defn -main
  "I don't do a whole lot ... yet."
  [num]
  (write-to-file (sieve-eratosthenes num)))
