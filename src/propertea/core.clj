(ns propertea.core
  (:require clojure.walk clojure.set clojure.string)
  (:import [java.io FileReader]
           [java.util Properties]))

(defn properties->map [props]
  (->> props (into {}) clojure.walk/keywordize-keys))

(defn file-name->properties [file-name]
  (doto (Properties.)
    (.load (FileReader. file-name))))

(defmulti valid? class :default :default)

(defmethod valid? String [a]
  (seq a))

(defmethod valid? nil [a]
  false)

(defmethod valid? :default [a]
  true)

(defn validate [m required-list]
  (let [ks (reduce (fn [r [k v]] (if (valid? v) (conj r k) r)) #{} m)
        rks (set required-list)]
    (if-let [not-found (seq (clojure.set/difference rks ks))]
      (throw (RuntimeException. (str not-found " are required, but not found")))
      m)))

(defn dump [m f]
  (when f
    (doseq [[k v] m]
      (f (pr-str k v))))
  m)

(defn parse-int-fn [v]
  (try
    (Integer/parseInt v)
    (catch NumberFormatException e
      nil)))

(defn parse-bool-fn [v]
  (when v
    (condp = (clojure.string/lower-case v)
        "true" true
        "false" false
        nil)))

(defn parse [m f ks]
  (reduce (fn [r e] (assoc r e (f (e r))))
          m
          ks))

(defn append [m defaults]
  (merge m (apply hash-map defaults)))

(defn read-properties [file-name & {:keys [dump-fn
                                           required
                                           parse-int
                                           parse-boolean
                                           default]}]
  (-> file-name
      file-name->properties
      properties->map
      (parse parse-int-fn parse-int)
      (parse parse-bool-fn parse-boolean)
      (append default)
      (validate required)
      (dump dump-fn)))
