(ns heatmap.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as string]))


(def api-base "https://api.moves-app.com/api/1.1")
(def token (string/trim (slurp "oauth_key.txt")))
(def token-query-param (str "?access_token=" token))

(def daily-summary-path "/user/summary/daily")
(def daily-storyline-path "/user/storyline/daily")

(def ex-day "20140501")

(def input-formatter (f/formatter "yyyyMMdd"))

(defn format-date [date] (f/unparse input-formatter date))

(defn trackpoint_path [day] (str api-base daily-storyline-path "/" day token-query-param "&trackPoints=true"))

(def week-str "2014-W22")

(defn todays-storyline
  "fetch storyline from today"
  []
  (json/read-str
   (->> ;(t/today-at 12 00)
        ;(format-date)
        week-str
        (trackpoint_path)
        (client/get)
        (:body))
   :key-fn keyword))

((defn todays-trackpoints
   "extract trackpoints from storyline"
   []
   (->> (todays-storyline) (first)
     (:segments)
     (map :activities) (flatten)
     (map :trackPoints) (flatten))))


; assoc (?) f/parse :time
; maybe aggregate nearby points?
; display with the java/clojurescripts? http://www.patrick-wied.at/static/heatmapjs/index.html
