(ns clj-game-of-life.core-spec
  (:use speclj.core
        clj-game-of-life.core))


(describe "world-width"
  (it "retuns the width of the world"
    (should= 3 (world-width (create-world 3 2)))))


(describe "world-height"
  (it "retuns the height of the world"
    (should= 2 (world-height (create-world 3 2)))))


(describe "element-at"
  (with test-world [[0 1]
                    [1 0]])

  (it "returns the element"
    (should= 1 (element-at @test-world [1 0]))))


(describe "alive?"
  (with test-world [[0 1]])

  (it "returns true if the element of location is 1"
    (should (alive? @test-world [1 0])))

  (it "returns false if the element of location is 0"
    (should-not (alive? @test-world [0 0]))))


(describe "neighborhood"
  (with test-world [[1 2 3]
                    [4 5 6]
                    [7 8 9]])

  (it "returns all elements around the locatio expect itself"
    (should= [1 4 7 2 8 3 6 9] (neighborhood @test-world [1 1])))

  (it "handles top left corner cases"
    (should= [4 2 5] (neighborhood @test-world [0 0])))

  (it "handles bottom right corner cases"
    (should= [5 8 6] (neighborhood @test-world [2 2]))))


(describe "alive-cells-around"
  (with test-world [[1 0 1]
                    [0 0 1]
                    [0 1 0]])

  (it "returns the sum of alive cells in the neighborhood"
    (should= 4 (alive-cells-around @test-world [1 1]))))


(describe "will-life?"
  ;; living cell
  (it "returns true if a living cell is surrounded by 2 living cells"
    (should (will-life? [[1 1 0]
                         [0 1 0]
                         [0 0 0]] [1 1])))
  (it "returns true if a living cell is surrounded by 3 living cells"
    (should (will-life? [[1 1 1]
                         [0 1 0]
                         [0 0 0]] [1 1])))
  (it "returns false if a living cell is surrounded by 1 living cell"
    (should-not (will-life? [[0 1 0]
                             [0 1 0]
                             [0 0 0]] [1 1])))
  (it "returns false if a living cell is surrounded by 4 living cell"
    (should-not (will-life? [[0 1 0]
                             [1 1 1]
                             [0 1 0]] [1 1])))
  ;; dead cell
  (it "returns true if a dead cell is surrounded by 3 living cell"
    (should (will-life? [[0 1 0]
                         [1 0 1]
                         [0 0 0]] [1 1])))
  (it "returns false if a dead cell is surrounded by 2 living cell"
    (should-not (will-life? [[0 1 0]
                             [1 0 0]
                             [0 0 0]] [1 1]))))


(describe "lifecycle"
  (it "kills living cells with less than 2 alive cells around"
    (should= [[0 0 0]
              [0 0 0]
              [0 0 0]] (lifecycle  [[0 1 0]
                                    [0 1 0]
                                    [0 0 0]])))

  (it "revives dead cells with 3 alive cells around"
    (should= [[0 0 0]
              [0 1 0]
              [0 0 0]] (lifecycle  [[0 1 0]
                                    [0 0 1]
                                    [1 0 0]]))))

(run-specs)