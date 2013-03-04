(ns clj-game-of-life.core
  (:use quil.core))

(def running true)
(def fps 20)
(def cell-sleep-time (round (/ 1000 2)))
(def dim 40)

(defrecord Cell [alive location])

(defn kill [cell]
  (assoc cell :alive 0))

(defn revive [cell]
  (assoc cell :alive 1))


(defn one-out-of [x]
  (if (>  (rand x)
          (- x 1))
    1
    0))

(def world
  (apply vector
    (map  (fn [y]
            (apply  vector
                (map (fn [x] (agent (Cell. (round (one-out-of 12))
                                           [x y])))
                     (range dim))))
          (range dim))))

(defn element
  [[x y]]
  (-> world (nth y) (nth x)))

(defn surrounding [location]
  (let [[cell-x cell-y] location
        x (max (- cell-x 1) 0)
        y (max (- cell-y 1) 0)
        width  (- (min (+ cell-x 2) dim) x)
        height (- (min (+ cell-y 2) dim) y)]
    (filter #(not= % location)
            (for [xi (range width)
                  yi (range height)]
              [(+ x xi) (+ y yi)]))))

(defn count-alive-cells [cells]
  (reduce 
    (fn [sum i] (+ sum i)) 0
    (map #(:alive %)
         cells)))

(defn switch-alive [cell]
  (assoc cell
    :alive (mod (inc (:alive cell))
                2)))

(defn live [cell]
  (do 
    (if (and  running
              (not (nil? *agent*)))
      (send *agent* live))
    (. Thread sleep cell-sleep-time)
    ; (println (:location cell))
    ; (println (surrounding (:location cell)))
    (let [surrounding-alive-count (count-alive-cells
                                    (map (fn [_] @_)
                                         (map element
                                            (surrounding (:location cell)))))]
      ; (println "alive count" surrounding-alive-count)
      (if (zero? (:alive cell))
        ;; dead cell. Needs 3 alice cells to revive
        (if (= surrounding-alive-count 3)
          (revive cell) ;; dead cell comes back to live
          cell) ;; dead cell remains dead
        ;; alive cell
        (if (or (= surrounding-alive-count 2)
                (= surrounding-alive-count 3))
          cell ;; alive cell still alive
          (kill cell)))))) ;; living cell dies

(defn go []
  (do
    (def running true)
    (dorun
      (map  (fn [cell-agent] (send cell-agent live))
            (flatten world)))
    "World started."))

(defn stop []
  (do
    (def running false)
    "World haltet"))

(defn nuke []
  "Kills all live."
  (map  (fn [cell-agent] (send-off cell-agent kill))
        (flatten world)))

(defn revive-coords [coords]
  (map  #(send (element %) revive)
          coords))

(defn sporn-block []
  (revive-coords [[0 0] [1 0] [0 1] [1 1]]))

(defn sporn-blinker []
  (dosync 
    (revive-coords [[1 1] [2 1] [3 1]])))

(defn setup []
  (smooth)
  (frame-rate fps)
  (background 0))

(defn draw []
  (stroke 0)
  (stroke-weight 0)
  (let [tile-width (/ (width) dim)
        tile-height (/ (height) dim)]
    (dorun
      (for [x (range dim)
            y (range dim)]
        (do
          (fill (* 255 (:alive @(element [x y]))))
          (rect (* x tile-width) (* y tile-height)
                tile-width tile-height))))))

(defn -main [& args]
  (defsketch example
  :title "Sketch"
  :setup setup
  :draw draw
  :size [(* dim 15) (* dim 15)])
  (go))
