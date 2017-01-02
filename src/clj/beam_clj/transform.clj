(ns beam-clj.transform
  (:import [beam_clj.ClojureDoFn]))

(defn dofn [process-element]
  (println (type process-element))
  (println (class process-element))
  (println (meta (symbol process-element)))
  (let [fn-ns (->> (resolve process-element) meta :ns str)
        fn-name (->> (resolve process-element) meta :name str)]

    (println "ns: " fn-ns)
    (println "name: " fn-name)
    )

  )
