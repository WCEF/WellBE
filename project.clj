(defproject wcef-well "0.3.0-SNAPSHOT"
  :description "Well demo with Exonum for hospitals"
  :url "https://cljsnode.herokuapp.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.async "0.3.465"]
                 [cljsjs/react "15.6.2-0"]
                 [cljsjs/react-dom "15.6.2-0"]
                 [cljsjs/react-dom-server "15.6.2-0"]
                 [cljsjs/create-react-class "15.6.2-0"]
                 [cljsjs/material-ui "0.19.2-0"]
                 [cljs-react-material-ui "0.2.48"
                  :exclusions [cljsjs/material-ui
                               org.clojure/clojure
                               org.clojure/clojurescript]]
                 [reagent "0.7.0"]
                 [secretary "1.2.3"]
                 [re-frame "0.10.3-beta1"]
                 [com.taoensso/timbre "4.10.0"]
                 [cljs-http "0.1.44"]
                 [com.taoensso/timbre "4.10.0"]
                 [kioo "0.5.0"
                  :exclusions [org.clojure/clojure cljsjs/react cljsjs/react-dom]]]

  :npm {:dependencies [[express "4.16.2"]
                       [xhr2 "0.1.4"]
                       [xmldom "0.1.27"]
                       [exonum-client "0.3.0"]
                       [react "16.2.0"]
                       [react-dom "16.2.0"]
                       [create-react-class "15.6.2"]
                       [source-map-support "0.5.0"]]
        :root :root}

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-npm "0.6.2"]]

  :min-lein-version "2.5.3"

  :hooks [leiningen.cljsbuild]

  :aliases {"start" ["npm" "start"]
            "test" ["with-profile" "test" "doo" "node" "server" "once"]}

  :main "main.js"

  :source-paths ["src/cljs"]

  :clean-targets ^{:protect false} [[:cljsbuild :builds :server :compiler :output-to]
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    "node_modules"
                                    :target-path :compile-path]

  :figwheel {:http-server-root "public"
             :css-dirs ["resources/public/css"]
             :server-logfile "logs/figwheel.log"
             :load-all-builds false
             :builds-to-start [:app :server]}

  :cljsbuild {:builds
              {:app
               {:source-paths ["src/browser" "src/cljs"]
                :compiler {:output-to "resources/public/js/out/app.js"
                           :output-dir "resources/public/js/out"
                           :asset-path "js/out"
                           :main app.start
                           :optimizations :none}}

               :server
               {:source-paths ["src/node" "src/cljs"]
                :compiler {:target :nodejs
                           :output-to "main.js"
                           :output-dir "target"
                           :main server.core
                           :foreign-libs [{:file "src/node/polyfill/simple.js"
                                           :provides ["polyfill.simple"]}]
                           :optimizations :none}}}}


  :profiles {:dev
             {:plugins
              [[lein-figwheel "0.5.14"]
               [lein-doo "0.1.8"]]
              :cljsbuild
              {:builds
               {:app
                {:compiler {:pretty-print true
                            :source-map true}
                 :figwheel true}
                :server
                {:compiler {:pretty-print true
                            :source-map true}
                 :figwheel {:heads-up-display false}}}}
              :npm {:dependencies [[ws "3.2.0"]]}}

             :test {:cljsbuild
                    {:builds
                     {:server
                      {:source-paths ["test"]
                       :compiler {:main runners.doo
                                  :optimizations :none
                                  :output-to "target/test/server.js"
                                  :output-dir "target/test"}}}}}

             :production
             {:env {:production true}
              :cljsbuild
              {:builds
               {:server
                {:compiler {;:optimizations :simple
                            :pretty-print false}}
                :app
                {:compiler {:output-dir "target/app/out"
                            :optimizations :advanced
                            :pretty-print false}}}}}})
