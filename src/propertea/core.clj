(ns propertea.core
  (:require clojure.walk clojure.set)
  (:import [java.io FileReader]
           [java.util Properties]))

(defn properties->map [props]
  (->> props (into {}) clojure.walk/keywordize-keys))

(defn file-name->properties [file-name]
  (doto (Properties.)
    (.load (FileReader. file-name))))

(defn validate [m required-list]
  (let [ks (set (keys m))
        rks (set required-list)]
    (if-let [not-found (seq (clojure.set/difference rks ks))]
      (throw (RuntimeException. (str not-found " are required, but not found")))
      m)))

(defn dump-if [m flag]
  (when flag
    (doseq [[k v] m]
      (println (str k "=" v))))
  m)

(defn parse [m f ks]
  (reduce (fn [r e] (assoc r e (f (e r))))
          m
          ks))

(defn read-properties [file-name & {:keys [dump
                                           required
                                           parse-int
                                           parse-boolean]}]
  (-> file-name
      file-name->properties
      properties->map
      (validate required)
      (dump-if dump)
      (parse #(Integer/parseInt %) parse-int)
      (parse #(Boolean/parseBoolean %) parse-boolean)))
