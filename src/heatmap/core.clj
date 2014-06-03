(ns heatmap.core
  (:require [clj-http.client :as client] [clojure.data.json :as json])
  (:use [clojure.string]))


(def api-base "https://api.moves-app.com/api/1.1")

(def daily-summary-path "/user/summary/daily")

(def day "/20140501")

(trim (slurp "oauth_key.txt"))
(def token (trim (slurp "oauth_key.txt")))

(def token-query-param (str "?access_token=" token))

((defn todays-trackpoints
  "fetch trackpoints from today"
  []
  (let [response (:body (client/get (str api-base daily-summary-path day token-query-param)))]
    (json/read-str response :key-fn  keyword)
    )))

(todays-trackpoints)
