(ns clj-game-of-life.core
  (:use quil.core))

(def dim 30)

(def world
  (apply vector
    (map
      (fn [_]
        (apply  vector
                (map (fn [_] (agent 0))
                (range dim))))
      (range dim))))

(defn element
  [x y]
  (-> world (nth y) (nth x)))

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
