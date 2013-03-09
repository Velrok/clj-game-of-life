(defproject clj-game-of-life "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main clj-game-of-life.core
  :dependencies [
    [org.clojure/clojure "1.4.0"]
    [quil "1.6.0"]
    [speclj "2.5.0"]]
  :test-paths ["spec"]
  :plugins [[speclj "2.5.0"]])
