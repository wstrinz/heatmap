(ns heatmap.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as stri]))


(def api-base "https://api.moves-app.com/api/1.1")
(def token (stri/trim (slurp "oauth_key.txt")))
(def token-query-param (str "?access_token=" token))

(def daily-summary-path "/user/summary/daily")
(def daily-storyline-path "/user/storyline/daily")

(def ex-day "20140501")

(def formatter (f/formatter "yyyyMMdd"))

(defn format-date [date] (f/unparse formatter date))

(defn trackpoint_path [day] (str api-base daily-storyline-path "/" day token-query-param "&trackPoints=true"))

(defn todays-storyline
  "fetch storyline from today"
  []
  (let [ response
         (:body (client/get (trackpoint_path (format-date (t/today-at 12 00)))))
         ]
    (json/read-str response :key-fn  keyword)
    ))


((defn todays-trackpoints
   "extract trackpoints from storyline"
   []
   (->> (todays-storyline) (first)
     (:segments)
     (map :activities) (flatten)
     (map :trackPoints) (flatten))))
