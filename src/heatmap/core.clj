(ns heatmap.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as string]
            [clojure.pprint :as pp]))

(def api-base "https://api.moves-app.com/api/1.1")
(def token (string/trim (slurp "oauth_key.txt")))
(def token-query-param (format "?access_token=%s" token))

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

(defn todays-trackpoints
   "extract trackpoints from storyline"
   []
   (->> (todays-storyline)
        first
        :segments
        (remove #(nil? (:activities %)))
        (mapcat :activities)
        (mapcat :trackPoints))
        )

(def thestuff
  (map #(update-in % [:time] f/parse) (todays-trackpoints)))

(defn coords
  [{:keys [lat lon]}]
  (vector lat lon))

(def just-coords
  (map coords (todays-trackpoints)))

(defn less-precise
  [coord]
  (let [fmtstring "##.#####"]
    (apply vector
           (map #(-> (DecimalFormat. fmtstring)
                     (.parse
                      (-> (DecimalFormat. fmtstring)
                          (.format %))))
                coord))))


; maybe aggregate nearby points?
; display with the java/clojurescripts? http://www.patrick-wied.at/static/heatmapjs/index.html
