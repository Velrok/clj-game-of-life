(ns clj-game-of-life.core
  (:use quil.core))

(def dim 3)

(defn element
  [x y]
  (-> world (nth y) (nth x)))

(defrecord Cell [alive location])

(defn revive [cell]
  (assoc cell :alive 1))

(defn kill [cell]
  (assoc cell :alive 0))

(defn surronding [location]
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

(def world
  (apply vector
    (map  (fn [y]
            (apply  vector
                (map (fn [x] (agent (Cell. 0 [x y])))
                     (range dim))))
          (range dim))))

(defn setup []
  (smooth)                          ;;Turn on anti-aliasing
  (frame-rate 1)                    ;;Set framerate to 1 FPS
  (background 0))                 ;;Set the background colour to
                                    ;;  a nice shade of grey.
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
  (defsketch example                  ;;Define a new sketch named example
  :title "Sketch"  ;;Set the title of the sketch
  :setup setup                      ;;Specify the setup fn
  :draw draw                        ;;Specify the draw fn
  :size [323 200]))                  ;;You struggle to beat the golden ratio
