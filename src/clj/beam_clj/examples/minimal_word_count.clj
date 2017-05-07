(ns beam-clj.examples.minimal-word-count
  (:import [beam_clj.ClojureDoFn]
           [org.apache.beam.runners.direct DirectRunner DirectOptions]
           [org.apache.beam.sdk Pipeline]
           [org.apache.beam.sdk.coders StringUtf8Coder]
           [org.apache.beam.sdk.io.TextIO]
           [org.apache.beam.sdk.options PipelineOptions PipelineOptionsFactory]
           [org.apache.beam.sdk.transforms Count DoFn MapElements ParDo SimpleFunction]
           [org.apache.beam.sdk.transforms.DoFn]
           [org.apache.beam.sdk.values KV]
           (org.apache.beam.runners.dataflow.options DataflowPipelineOptions)
           (org.apache.beam.runners.dataflow DataflowRunner)
           (org.apache.beam.sdk.io TextIO$Read TextIO$Write))
  (:require [clojure.string :as string]
            [beam-clj.transform :as transform]
            [clojure.java.io :as io]))

;; https://github.com/apache/incubator-beam/blob/master/examples/java/src/main/java/org/apache/beam/examples/MinimalWordCount.java
(defn pipeline-options [{:keys [tmp]}]
  (doto (PipelineOptionsFactory/create)
    (.as DirectOptions)
    (.setTempLocation tmp)
    (.setRunner DirectRunner)))

(defn dataflow-pipeline-options [{:keys [tmp]}]
  (doto ^DataflowPipelineOptions (PipelineOptionsFactory/create)
    (.as DataflowPipelineOptions)
    (.setTempLocation tmp)
    (.setRunner DataflowRunner)))

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

(defn do-it []
  (let [p (Pipeline/create (pipeline-options {:tmp "/tmp" #_"gs://test-dflow"}))]
    (-> p
      (.apply (TextIO$Read/from
                "./sample-data/shakespeare.txt"))
      (.apply "ExtractWords" (-> extract-words transform/dofn ParDo/of))
      (.setCoder (StringUtf8Coder/of))
      (.apply (Count/perElement))
      (.apply "FormatResult" (-> format-kv transform/dofn ParDo/of))
      (.setCoder (StringUtf8Coder/of))
      (.apply (TextIO$Write/to
                "./sample-data/out.txt")))
    (.run p)
    )
  )


(comment
  ;; Want it to look like this:
  (-> (pipeline-options {:tmp "/tmp" #_"gs://test-dflow"})
    Pipeline/create
    (.apply (beamio/read-text "./sample-data/shakespeare.txt"))
    (.apply "ExtractWords" (-> extract-words transform/dofn transform/pardo))
    (.apply (Count/perElement))
    (.apply "FormatResult" (-> format-kv-fn transform/dofn transform/pardo))
    (.apply (beamio/write-text "./sample-data/out.txt"))))
