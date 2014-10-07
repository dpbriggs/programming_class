(ns sieve-of-atkin.core
  (:gen-class))

(def limit 1000000)

(defn candidate-primes
  "Uses quadratic forms to find likely primes"
  [limit x y]
  (let [test-1 (+ (* 4 x x) (* y y))
	test-2 (+ (* 3 x x) (* y y))
	test-3 (- (* 3 x x) (* y y))]
    (cond (and (<= test-1 limit) (or (= 1 (mod test-1 12))
				     (= 5 (mod test-1 12))))
	  test-1

	  (and (<= test-2 limit) (= 7 (mod test-2 12)))
	  test-2

	  (and (<= test-3 limit) (> x y) (= 11 (mod test-3 12)))
	  test-3)))

(defn get-candidate-primes
  [n]
  (let [lim (Math/ceil (Math/pow n 0.5))
	primes (for [x (range 1 lim)
		     y (range 1 lim)]
		 (candidate-primes n x y))]
    (filter #(not (nil? %)) primes)))

(defn sieve-of-atkin
  [n]
  (let [cand-primes (get-candidate-primes n)
	nums (range 10)]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
