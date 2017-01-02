(ns beam-clj.examples.minimal-word-count
  (:import [beam_clj.ClojureDoFn]
           [org.apache.beam.runners.direct DirectRunner DirectOptions]
           [org.apache.beam.sdk Pipeline]
           [org.apache.beam.sdk.coders StringUtf8Coder]
           [org.apache.beam.sdk.io.TextIO]
           [org.apache.beam.sdk.options PipelineOptions PipelineOptionsFactory]
           [org.apache.beam.sdk.transforms Count DoFn MapElements ParDo SimpleFunction]
           [org.apache.beam.sdk.transforms.DoFn]
           [org.apache.beam.sdk.values KV])
  (:require [clojure.string :as string]))

;; https://github.com/apache/incubator-beam/blob/master/examples/java/src/main/java/org/apache/beam/examples/MinimalWordCount.java

(defn pipeline-options []
  (doto (PipelineOptionsFactory/create)
    (.as DirectOptions)
    (.setTempLocation "/tmp")
    (.setRunner DirectRunner)))

(defn words [line]
  (filter (complement string/blank?) (string/split line #"[^a-zA-Z']+")))


(defn extract-words [ctx]
  (let [w (-> ctx (.element) words)]
    (dorun (map #(.output ctx %) w))
    )
  )

(defn format-kv [ctx]
  (let [kv (.element ctx)
        output (str (.getKey kv) " " (.getValue kv))]
    (.output ctx output)
    )
  )

(defn format-kv-fn []
  (beam_clj.ClojureDoFn. "beam-clj.examples.minimal-word-count" "format-kv")
  )

(defn dofn [] (beam_clj.ClojureDoFn. "beam-clj.examples.minimal-word-count" "extract-words"))

(defn do-it []
  (let [p (Pipeline/create (pipeline-options))]
    (-> p
        (.apply (org.apache.beam.sdk.io.TextIO$Read/from
                 "./sample-data/shakespeare.txt"))
        (.apply "ExtractWords" (ParDo/of (dofn)))
        (.setCoder (StringUtf8Coder/of))
        (.apply (Count/perElement))
        (.apply "FormatResult" (ParDo/of (format-kv-fn)))
        (.setCoder (StringUtf8Coder/of))
        (.apply (org.apache.beam.sdk.io.TextIO$Write/to
                 "./sample-data/out.txt"))

        )
    (.run p)
    )
  )
