(ns clj-game-of-life.core
  (:use quil.core))

(def running true)

(defrecord Cell [alive location])

(defn kill [cell]
  (assoc cell :alive 0))

(defn revive [cell]
  (assoc cell :alive 1))

(def dim 3)

(def world
  (apply vector
    (map  (fn [y]
            (apply  vector
                (map (fn [x] (agent (Cell. 0 [x y])))
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
    (for [xi (range width)
          yi (range height)]
      [(+ x xi) (+ y yi)])))

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
  (when running
    (println "I'm living " cell)
    ; (. Thread sleep 500)
    ; (send-off *agent* live)
    (println (:location cell))
    (println (surrounding (:location cell)))
    (let [surrounding-alive-count (count-alive-cells
                                    (map (fn [_] @_)
                                         (map element
                                            (surrounding (:location cell)))))]
      (println "alive count" surrounding-alive-count)
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

(defn setup []
  (smooth)
  (frame-rate 1)
  (background 0))

(defn draw []
  (stroke 0)
  (stroke-weight 0)
  (let [tile-width (/ (width) dim)
        tile-height (/ (height) dim)]
    (doall
      (for [x (range dim)
            y (range dim)]
        (do
          (fill (* 255 @(element x y)))
          (rect (* x tile-width) (* y tile-height)
                tile-width tile-height))))))

(defn -main [& args]
  (defsketch example
  :title "Sketch"
  :setup setup
  :draw draw
  :size [323 200]))
