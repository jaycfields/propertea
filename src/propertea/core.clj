(ns propertea.core
  (:require clojure.walk clojure.set clojure.string)
  (:import [java.io FileReader]
           [java.util Properties]))

(defn- properties->map [props nested]
  (->>
   props
   (reduce (fn [r [k v]]
             (if nested
               (assoc-in r (clojure.string/split k #"\.") v)
               (assoc r k v)))
           {})
   clojure.walk/keywordize-keys))

(defn- file-name->properties [file-name]
  (doto (Properties.)
    (.load (FileReader. file-name))))

(defmulti valid? class :default :default)

(defmethod valid? String [a]
  (seq a))

(defmethod valid? nil [a]
  false)

(defmethod valid? :default [a]
  true)

(defn- validate [m required-list]
  (let [ks (reduce (fn [r [k v]] (if (valid? v) (conj r k) r)) #{} m)
        rks (set required-list)]
    (if-let [not-found (seq (clojure.set/difference rks ks))]
      (throw (RuntimeException. (str not-found " are required, but not found")))
      m)))

(defn- dump [m f]
  (when f
    (doseq [[k v] m]
      (f (pr-str k v))))
  m)

(defn- parse-int-fn [v]
  (try
    (Integer/parseInt v)
    (catch NumberFormatException e
      nil)))

(defn- parse-bool-fn [v]
  (when v
    (condp = (clojure.string/lower-case v)
        "true" true
        "false" false
        nil)))

(defn- parse [m f ks]
  (reduce
   (fn [r e]
     (if (contains? r e)
       (assoc r e (f (e r)))
       r))
   m
   ks))

(defn map->properties [m]
  (reduce
   (fn [r [k v]]
     (cond
      (keyword? k) (.put r (name k) v)
      (symbol? k) (.put r (name k) v)
      :else (.put r k v))
     r)
   (Properties.)
   m))

(defn- append [m defaults]
  (merge (apply hash-map defaults) m))

(defn read-properties [file-name & {:keys [dump-fn
                                           required
                                           parse-int
                                           parse-boolean
                                           nested
                                           default]}]
  (-> file-name
      file-name->properties
      (properties->map nested)
      (parse parse-int-fn parse-int)
      (parse parse-bool-fn parse-boolean)
      (append default)
      (validate required)
      (dump dump-fn)))
