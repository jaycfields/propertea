(ns propertea.core
  (:require clojure.walk clojure.set clojure.string)
  (:import [java.io FileReader]
           [java.util Properties]))

(defn- keywordize-keys-unless [m b]
  (if b
    m
    (clojure.walk/keywordize-keys m)))

(defn- dash-match [[ _ g1 g2]]
  (str g1 "-" g2))

(defn- dasherize [k]
  (-> k
      (clojure.string/replace #"([A-Z]+)([A-Z][a-z])" dash-match)
      (clojure.string/replace #"([a-z\d])([A-Z])" dash-match)
      (clojure.string/lower-case)))

(defn- properties->map [props nested kf]
  (reduce (fn [r [k v]]
            (if nested
              (assoc-in r (clojure.string/split (kf k) #"\.") v)
              (assoc r (kf k) v)))
          {}
          props))

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
      (keyword? k) (.put r (name k) (str v))
      (symbol? k) (.put r (name k) (str v))
      :else (.put r (str k) (str v)))
     r)
   (Properties.)
   m))

(defn- append [m defaults]
  (merge (apply hash-map defaults) m))

(defn read-properties [file-name & {:keys [dump-fn
                                           required
                                           parse-int
                                           parse-boolean
                                           stringify-keys
                                           dasherize-keys
                                           nested
                                           default]}]
  (-> file-name
      file-name->properties
      (properties->map nested (if dasherize-keys dasherize identity))
      (keywordize-keys-unless stringify-keys)
      (parse parse-int-fn parse-int)
      (parse parse-bool-fn parse-boolean)
      (append default)
      (validate required)
      (dump dump-fn)))
