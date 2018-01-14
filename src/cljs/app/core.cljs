(ns app.core
  (:require-macros
   [cljs.core.async.macros
    :refer [go go-loop]])
  (:require
   [cljs.core.async :as async
    :refer [<! chan close! alts! timeout put!]]
   [goog.dom :as dom]
   [reagent.core :as reagent
    :refer [atom]]
   [api.jokes :as jokes]
   [app.session :as session]
   [app.view.page
    :refer [page html5]]
   [app.view.view
    :refer [view]]))

(defn scripts [initial]
  [{:src "/js/out/app.js"}
   (str "main_cljs_fn("
        (pr-str (pr-str initial))
        ")")])

(def default-state
  {:mode "split"
   :stage "checkout"
   :patient 5
   :itinerary {:items [{:label "1"}
                       {:label "2"}]}})

(defn static-page []
  (go-loop []
    (let [initial default-state
          state (session/state initial)]
      (-> state
          (page :scripts (scripts initial)
                :title "WellBE"
                :forkme false)
          (html5)))))

(defn activate [initial]
  (session/initialize initial)
  (let [el (dom/getElement "canvas")
        content (cljs.reader/read-string initial)
        state (session/state default-state)]
    (reagent/render [#(view state)] el)))
