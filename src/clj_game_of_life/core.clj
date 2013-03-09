(ns clj-game-of-life.core
  (:use quil.core))

(def running true)
(def fps 20)
(def dim 40)


(defn create-world [width height]
  (apply vector
              (map  (fn [y]
                      (apply  vector
                          (map (fn [x] 0)
                               (range width))))
                    (range height))))

(def world
  (ref (apply vector
              (map  (fn [y]
                      (apply  vector
                          (map (fn [x] 0)
                               (range dim))))
                    (range dim)))))


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

(defn lifecycle[world]
  (apply  vector
          (map  (fn [y]
                  (apply  vector
                          (map (fn [x] (if (> 0 (alive-cells-around world [x y]))
                                            1
                                            0))
                               (range (count (nth world y))))))
                (range (count world)))))

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



(defn element
  ([[x y]]
    (-> @world (nth y) (nth x)))
  ([x y]
    (element [x y])))

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
    cells))

; (defn live [cell]
;   (do
;     (if (and  running
;               (not (nil? *agent*)))
;       (send *agent* live))
;     (. Thread sleep cell-sleep-time)
;     ; (println (:location cell))
;     ; (println (surrounding (:location cell)))
;     (let [surrounding-alive-count (count-alive-cells
;                                     (map (fn [_] @_)
;                                          (map element
;                                             (surrounding (:location cell)))))]
;       ; (println "alive count" surrounding-alive-count)
;       (if (zero? (:alive cell))
;         ;; dead cell. Needs 3 alice cells to revive
;         (if (= surrounding-alive-count 3)
;           (revive cell) ;; dead cell comes back to live
;           cell) ;; dead cell remains dead
;         ;; alive cell
;         (if (or (= surrounding-alive-count 2)
;                 (= surrounding-alive-count 3))
;           cell ;; alive cell still alive
;           (kill cell)))))) ;; living cell dies

; (defn go []
;   (do
;     (def running true)
;     (dorun
;       (map  (fn [cell-agent] (send cell-agent live))
;             (flatten world)))
;     "World started."))

; (defn stop []
;   (do
;     (def running false)
;     "World haltet"))

; (defn nuke []
;   "Kills all live."
;   (map  (fn [cell-agent] (send-off cell-agent kill))
;         (flatten world)))

; (defn revive-coords [coords]
;   (map  #(send (element %) revive)
;           coords))

; (defn sporn-block []
;   (revive-coords [[0 0] [1 0] [0 1] [1 1]]))

; (defn sporn-blinker []
;   (dosync
;     (revive-coords [[1 1] [2 1] [3 1]])))

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
  :size [(* dim 15) (* dim 15)]))
