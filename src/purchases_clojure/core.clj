(ns purchases-clojure.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))

(defn read-purchases []
  (let [purchases (slurp "purchases.csv")
        purchases (str/split-lines purchases)
        purchases (map (fn [line] 
                         (str/split line #","))
                    purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line] 
                         (zipmap header line))
                     purchases)]
    purchases))
        ;input (read-line)
        ;purchases (filter #(= input (get % "category"))
         ;           purchases)
        ;text (pr-str purchases)]))

(defn purchases-html [purchases]
  [:html
   [:body
    [:a {:href "/"} "All"]
    " "
    [:a {:href "/Furniture"} "Furniture"]
    " "
    [:a {:href "/Alcohol"} "Alcohol"]
    " "
    [:a {:href "/Toiletries"} "Toiletries"]
    " "
    [:a {:href "/Shoes"} "Shoes"]
    " "
    [:a {:href "/Food"} "Food"]
    " "
    [:a {:href "/Jewelry"} "Jewelry"]
    [:ol
     (map (fn [purchase]
            [:li (str (get purchase "customer_id")
                   " "
                   (get purchase "date")
                   " "
                   (get purchase "credit_card")
                   " "
                   (get purchase "cvv")
                   " "
                   (get purchase "category"))])
       purchases)]]])
    

(defn filter-by-category [purchases category]
  (filter (fn [purchase]
            (= category (get purchase "category")))
    purchases))

(c/defroutes app
  (c/GET "/:category{.*}" [category]
    (let [purchases (read-purchases)
          purchases (if (= 0 (count category))
                      purchases
                      (filter-by-category purchases category))]
      (h/html (purchases-html purchases)))))

(defonce server (atom nil))

(defn -main []
  (if @server
    (.stop @ server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))
