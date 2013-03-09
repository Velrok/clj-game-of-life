(ns clj-game-of-life.core
  (:use quil.core))


(defn create-world [width height]
  (apply vector
              (map  (fn [y]
                      (apply  vector
                          (map (fn [x] 0)
                               (range width))))
                    (range height))))


(defn world-width [world]
  (count (nth world 0)))


(defn world-height [world]
  (count world))


(defn neighborhood [world [cell-x cell-y]]
  (let [world-height (count world)
        world-width (count (nth world 0))
        x (max (- cell-x 1) 0)
        y (max (- cell-y 1) 0)
        width  (- (min (+ cell-x 2) world-width) x)
        height (- (min (+ cell-y 2) world-height) y)]
    (map (fn [[x y]] (-> world (nth y) (nth x)))
         (filter  #(not= % [cell-x cell-y])
                  (for [xi (range width)
                        yi (range height)]
                    [(+ x xi) (+ y yi)])))))


(defn alive-cells-around [world location]
  (reduce + 0
          (neighborhood world location)))

(defn element-at [world [x y]]
  (-> world (nth y) (nth x)))


(defn alive? [world location]
  (== (element-at world location) 1))


(defn will-life? [world location]
  (if (alive? world location)
    ;; alive cell
    (let [alive-cells-count (alive-cells-around world location)]
      (or (== 2 alive-cells-count)
          (== 3 alive-cells-count)))
    ;; dead cell
    (== 3 (alive-cells-around world location))))

(defn lifecycle[world]
  (apply  vector
          (map  (fn [y]
                  (apply  vector
                          (map (fn [x] (if (will-life? world [x y])
                                            1
                                            0))
                               (range (count (nth world y))))))
                (range (count world)))))


(def fps 20)
(def world (create-world 50 40))


(defn setup []
  (smooth)
  (frame-rate fps)
  (background 0))

(defn draw []
  (stroke 0)
  (stroke-weight 0)
  (let [tile-width (/ (width) (world-width world))
        tile-height (/ (height) (world-height world))]
    (dorun
      (for [x (range (world-width world))
            y (range (world-height world))]
        (do
          (fill (* 255 (element-at [x y])))
          (rect (* x tile-width) (* y tile-height)
                tile-width tile-height))))))

(defn -main [& args]
  (defsketch example
  :title "Sketch"
  :setup setup
  :draw draw
  :size [(* (world-width world) 15) (* (world-height world) 15)]))
