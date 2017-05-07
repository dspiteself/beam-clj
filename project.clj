(defproject beam-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.beam/beam-sdks-java-core "0.6.0"]]
  :profiles {:dev {:source-paths ["dev"]}
             :provided
             {:dependencies [[org.apache.beam/beam-runners-google-cloud-dataflow-java "0.6.0"]
                             [org.apache.beam/beam-runners-direct-java "0.6.0"]]}})
