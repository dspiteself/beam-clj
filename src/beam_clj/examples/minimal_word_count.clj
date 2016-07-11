(ns beam-clj.examples.minimal-word-count
  (:import [org.apache.beam.runners.direct InProcessPipelineRunner InProcessPipelineOptions]
           [org.apache.beam.sdk Pipeline]
           [org.apache.beam.sdk.io.TextIO]
           [org.apache.beam.sdk.options PipelineOptions PipelineOptionsFactory]
           [org.apache.beam.sdk.transforms.DoFn]
           [org.apache.beam.sdk.transforms Count DoFn MapElements ParDo SimpleFunction]
           [org.apache.beam.sdk.values KV])
  (:require [clojure.string :as string]))

;; https://github.com/apache/incubator-beam/blob/master/examples/java/src/main/java/org/apache/beam/examples/MinimalWordCount.java

(defn pipeline-options []
  (doto (PipelineOptionsFactory/create)
    (.as InProcessPipelineOptions)
    (.setTempLocation "/tmp")
    (.setRunner InProcessPipelineRunner)))

(defn words [line]
  (filter true? (string/split line #"[^a-zA-Z']+")))

(defn ExtractWords []
  (proxy [DoFn] []
    (processElement [ctx]
      (let [words (-> ctx (.element) words)]
        (dorun (map #(.output ctx %) words))
        )
      )

    ))

(defn do-it []
  (let [p (Pipeline/create (pipeline-options))]
    (doto p
      (.apply (org.apache.beam.sdk.io.TextIO$Read/from "./sample-data/shakespeare.txt"))
      (.apply "ExtractWords" (ParDo/of (ExtractWords)))
      (.run)
      )

    )
  )
