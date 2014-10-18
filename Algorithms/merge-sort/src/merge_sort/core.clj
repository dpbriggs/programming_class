(ns merge-sort.core
  (:gen-class))

(defn merge
  [[l-first & left :as L] [r-first & right :as R]]
  (if (nil? left)
    ))

(defn merge-sort
  [coll]
  (if (= 1 (count coll))
    coll
    (let [middle (quot (count coll) 2)
          [left right] (split-at middle coll)
          l1 (merge-sort left)
          l2 (merge-sort right)]
      (println l1 " && " l2))))
