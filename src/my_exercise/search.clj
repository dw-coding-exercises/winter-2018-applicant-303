(ns my-exercise.search
  "The code behind the search button."
  (:require [clojure.string :refer [join lower-case replace]]
            [clj-http.client :as client]))


(defn address
  "extracts the address from the params"
  [params]
  (select-keys params [:street :street-2 :city :state :zip]))

(defn address->ocd-id-country
  "Assumes all address are in the U.S."
  [address]
  "ocd-division/country:us")

(defn address->ocd-id-state
  "Assumes addresses are in the U.S."
  [address]
  (str "ocd-division/country:us/state:"
       (lower-case (:state address))))

(defn sanitize-place
  [place]
  (lower-case (replace place #" " "")))

(defn address->ocd-id-place
  "Assumes addresses are in the U.S., punting on spaces"
  [address]
  (str "ocd-division/country:us/state:"
       (lower-case (:state address))
       "/place:"
       (sanitize-place (:city address))))

(defn address->ocd-ids
  [address]
  (join "," (map #(% address) [address->ocd-id-country
                               address->ocd-id-state
                               address->ocd-id-place])))


(def turbovote-url
  "The url for retrieving elections from turbovote."
  "https://api.turbovote.org/elections/upcoming?district-divisions=")

(defn hey-turbovote-wheres-my-elections
  [address]
  (:body 
   (client/get (str turbovote-url
                    (address->ocd-ids address)))))

(defn find-election
  "finds an election"
  [args]
  (println)
  (println "finding: "
           (hey-turbovote-wheres-my-elections args))
  "OK")
