(ns propertea.test.core
  (:use [propertea.core] :reload)
  (:use [expectations])
  (:require clojure.java.io))

(def fp (clojure.java.io/file "test/fake.properties"))

;;; read a string
(expect "hello-string" (:string-example (read-properties fp)))

;;; read and convert the string into an int
(expect 1 (:int-example (read-properties fp :parse-int [:int-example])))

;;; read and convert an invalid int string into nil
(expect nil (:string-example (read-properties fp :parse-int [:string-example])))

;;; read and convert the string into a boolean
(expect true? (:boolean-example (read-properties fp :parse-boolean [:boolean-example])))

;;; read and convert an invalid bool string into nil
(expect nil (:string-example (read-properties fp :parse-boolean [:string-example])))

;;; add nil to the properties if attempting to int parse a non-existent value
(expect nil (:l (read-properties fp :parse-int [:l])))

;;; add nil to the properties if attempting to bool parse a non-existent value
(expect nil (:l (read-properties fp :parse-boolean [:l])))

;;; include a default value if a value doesn't exist
(expect :def-val (:l (read-properties fp :default [:l :def-val])))

;;; don't include a default value if a value does exist
(expect "hello-string"
        (:string-example (read-properties fp :default [:string-example :def-val])))

;;; include a default value even if parsing fails due to it not existing
(expect true? (:l (read-properties fp :default [:l true] :parse-boolean [:l])))

;;; throw an exception if something is required and doesn't exist
(expect RuntimeException (read-properties fp :required [:foo :int-example]))

;;; throw an exception if something exists but is an empty string
(expect RuntimeException (read-properties fp :required [:empty-string]))

;;; throw an exception if invalid parsing occurs, resulting in nil
;;; and it is also required
(expect RuntimeException
        (read-properties fp :required [:string-example] :parse-int [:string-example]))

;;; show me the properties
(expect java.util.Properties (map->properties {"A" 1 "B" 2}))

;;; get a value
(expect 1 (.get (map->properties {"A" 1 "B" 2}) "A"))

;;; get a value after converting keywords to strings
(expect 1 (.get (map->properties {:A 1 :B 2}) "A"))

;;; get a value after converting symbols to strings
(expect 1 (.get (map->properties {'A 1 'B 2}) "A"))

;;; nest map
(expect "5"
        (get-in
         (read-properties fp :nested true)
         [:nested :example :depth]))

;;; nest map
(expect "2"
        (get-in
         (read-properties fp :nested true)
         [:nested :example :leaves]))

(expect [String]
        (->> (read-properties fp :stringify-keys true)
            keys
            (map class)
            distinct))

(expect "get-dashed"
        (get-in
         (read-properties fp :nested true :dasherize-keys true)
         [:nested :with-camel-case]))

(expect "get-dashed"
        (get-in
         (read-properties fp :nested true)
         [:nested :withCamelCase]))
