(ns beam-clj.transform
  (:import (beam_clj ClojureDoFn)))

(defmacro dofn [process-element]
  (let [defn-var  (resolve process-element)
        defn-meta (meta defn-var)
        fn-ns (->> defn-meta :ns str)
        fn-name (->> defn-meta :name str)]
    (assert (and fn-ns fn-name) "function passed to dofn must be defed")
    `(ClojureDoFn. ~fn-ns ~fn-name)))
