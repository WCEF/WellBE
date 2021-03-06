(ns server.core
  (:require-macros
   [cljs.core.async.macros :as m
    :refer [go go-loop alt!]])
  (:require
   [polyfill.compat]
   [cljs.nodejs :as nodejs]
   [cljs.core.async :as async
    :refer [chan close! timeout put!]]
   [reagent.core :as reagent
    :refer [atom]]
   [api.exonum-client :as exonum-client]
   [api.well :as well]
   [app.core :as app
    :refer [static-page]]))

(enable-console-print!)

(def express (nodejs/require "express"))

(defn handler [req res]
  (if (= "https" (aget (.-headers req) "x-forwarded-proto"))
    (.redirect res (str "http://" (.get req "Host") (.-url req)))
    (go
      (.set res "Content-Type" "text/html")
      (.send res (<! (static-page))))))

(defn pay-handler [req res]
  (let [payment
        (exonum-client/Payment
         {:from "6752be882314f5bbbc9a6af2ae634fc07038584a4a77510ea5eced45f54dc030"
          :to "f5864ab6a5a2190666b47c676bcf15a1f2f07703c5bcafb5749aa735ce8b7c36"
          :amount 15})]
    (.serialize payment)))

(defn server [port success]
  (doto (express)
    (.get "/" handler)
    (.get "/api/exonum/pay" pay-handler)
    (.use (.static express "resources/public"))
    (.listen port success)))

(defn -main [& mess]
  (let [port (or (.-PORT (.-env js/process)) 1337)]
    (server port
            #(println (str "Server running at http://127.0.0.1:" port "/")))))

(set! *main-cli-fn* -main)
