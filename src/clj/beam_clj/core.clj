(ns beam-clj.core
  (:require [beam-clj.examples.minimal-word-count :as wc]))

(defn -main [& args]
  (wc/do-it)

  )
