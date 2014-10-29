(ns vindinium.core
  (:gen-class)
  (:use [slingshot.slingshot :only [try+, throw+]])
  (:use [clojure.core.match :only (match)]))


;;;;;;;;;;;;;;;;;;;
;; Key: wb1ghxl5
;;;;;;;;;;;;;;;;;;;


(require '[clj-http.client :as http])

(def server-url "http://vindinium.org")

(defn at [[x y] tiles size]
 (tiles (+ (* y size) x)))

;;;;;;;;;;;;;;
;; My stuff
;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;
;;;;;; Algorithm
;;;;;;;;;;;;;;;;;;;;;

;
;
;      / local matrix ----- immediate goals*
; --1-@
;      \ global matrix ---- long terms goals**
;
; ------- Legend -------
;
; 1  Game map with values generated
; @  Various game logic, updates values
;    | health, enemy distance, mines owned
; *  Goals that can be accomplished in one turn
; ** Determined if no short term goals available
;    | choose closest safe goal (depends on health,
;    |                           distance, turn, mines owned)

; TODO: Handle mine being owned by another player
;       Add long term goals

(def last-turn (atom {}))

(def game-ai-values
  {:mine-unowned 6
   :mine-owned 0
   :enemy-low-health 5
   :enemy-high-health 1.5
   :tavern-low-health 4
   :tavern-high-health 2})

(defn need-tavern?
  "Determines if it's necessary to visit a tavern"
  [input]
  (let [life (get-in input [:hero :life])
        gold (get-in input [:hero :gold])]
    (if (> gold 2)
      (if (not= life 100)
        (if (< life 65)
          (:tavern-low-health game-ai-values)
          (:tavern-high-health game-ai-values))
        0) ;; If life is full no need for tavern
      0))) ;; No gold --> can't pay for tavern

(defn attack-player-value
  "Determines worth of attacking player on tile"
  [input tile]
  (let [id (:id tile)
        hero-id (get-in input [:hero :id])]
    (if (= id hero-id)
      0 ; No need to attack myself
      (let [my-health (get-in input [:hero :life])
            enemy-health (-> (dec id)
                             ((vec (get-in input [:game :heroes])))
                             :life)]
        (if (>= (- 30 my-health) enemy-health)
          (:enemy-low-health game-ai-values)
          (:enemy-high-health game-ai-values))))))

(defn assign-immediate-value
  "Takes a tile and assigns a value"
  [input tile]
  (match tile
         {:tile :hero :id _} (attack-player-value input tile)
         {:tile :mine :of _} (:mine-owned game-ai-values)
         {:tile :mine} (:mine-unowned game-ai-values)
         {:tile :tavern} (need-tavern? input)
         {:tile :wall} 0
         nil 0
         :else 1))

(defn gen-local-matrix
  "Generates vector of valued tiles around player"
  [input]
  (let [hero-pos (get-in input [:hero :pos])
        tiles (get-in input [:game :board :tiles])
        size (get-in input [:game :board :size])

        north (at [(hero-pos 0) (inc (hero-pos 1))] tiles size)
        south (at [(hero-pos 0) (dec (hero-pos 1))] tiles size)
        west (at [(dec (hero-pos 0)) (hero-pos 1)] tiles size)
        east (at [(inc (hero-pos 0)) (hero-pos 1)] tiles size)]
    [{:dir "north" :tile north :value (assign-immediate-value input north)}
     {:dir "south" :tile south :value (assign-immediate-value input south)}
     {:dir "east"  :tile east  :value (assign-immediate-value input east)}
     {:dir "west"  :tile west  :value (assign-immediate-value input west)}]))

(defn immediate-interesting-moves?
  "Determines if we can immediately make an action"
  [local-matrix]
  (if (some #(< 1 (:value %)) local-matrix)
    {:bool true :dir (-> (sort-by :value local-matrix)
                         last
                         :dir)}
    {:bool false :dir nil}))

(defn move
  "Handles moving, if direction not specified move randomly"
  [tile]
  (println tile)
  (if (:dir tile)
    (:dir tile)
    (first (shuffle ["north", "south", "east", "west", "stay"]))))

(defn handle-immediate-moves
  [input]
  (let [local-matrix (gen-local-matrix input)
        immediate-move (immediate-interesting-moves? local-matrix)]
    (if (:bool immediate-move)
      (move immediate-move)
      (move immediate-move) ;TODO delegate long term task
      )))

(defn bot [input]
  "Implement this function to create your bot!"
  (swap! last-turn (fn [n] input))
  (println (gen-local-matrix input))
  (handle-immediate-moves input))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vindinium starter functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


; Because the (y,x) position of the server is inversed. We fix it to (x,y).
(defn fix-pos [{:keys [x y]}] [y x])

(defn fix-hero [hero]
  (-> hero
      (update-in [:pos] fix-pos)
      (update-in [:spawnPos] fix-pos)))

(defn improve-input [input]
  (-> input
      (update-in [:hero] fix-hero)
      (update-in [:game :heroes] #(map fix-hero %))
      (update-in [:game :board :tiles] vec)))

(defn parse-tile [tile]
  (match (vec tile)
         [\space \space] {:tile :air}
         [\# \#] {:tile :wall}
         [\[ \]] {:tile :tavern}
         [\$ \-] {:tile :mine}
         [\$ i] {:tile :mine :of i}
         [\@ i] {:tile :hero :id (Integer/parseInt (str i))}))

(defn parse-tiles [tiles] (map parse-tile (partition 2 (seq tiles))))

(defn parse-input [input] (update-in input [:game :board :tiles] parse-tiles))

(defn request [url, params]
  "makes a POST request and returns a parsed input"
  (try+
    (-> (http/post url {:form-params params :as :json})
        :body
        parse-input
        improve-input)
    (catch map? {:keys [status body]}
      (println (str "[" status "] " body))
      (throw+))))


(defn step [from]
  (loop [input from]
    ;(print ".")
    (let [next (request (:playUrl input) {:dir (bot input)})]
      (if (:finished (:game next)) (println "") (recur next)))))

(defn training [secret-key turns]
  (let [input (request (str server-url "/api/training") {:key secret-key :turns turns})]
    (println (str "Starting training game " (:viewUrl input)))
    (step input)
    (println (str "Finished training game " (:viewUrl input)))))

(defn arena [secret-key games]
  (loop [it 1]
    (let [p #(println (str "[" it "/" games "] " %))
          _ (p "Waiting for pairing...")
          input (request (str server-url "/api/arena") {:key secret-key})]
      (p (str "Starting arena game " (:viewUrl input)))
      (step input)
      (p (str "Finished arena game " (:viewUrl input)))
      (when (< it games) (recur (+ it 1))))))

(def usage
  "Usage:
   training <secret-key> <number-of-turns>
   arena <secret-key> <number-of-games")

(defn -main [& args]
  (match (vec args)
         ["training", secret-key, nb] (training secret-key nb)
         ["arena", secret-key, nb] (arena secret-key nb)
         :else (println usage)))
