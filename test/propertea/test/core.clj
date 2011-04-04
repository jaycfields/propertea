(ns propertea.test.core
  (:use [propertea.core] :reload)
  (:use [expectations])
  (:require clojure.java.io))

(def fp (clojure.java.io/file "test/fake.properties"))

(expect "hello-string" (:string-example (read-properties fp)))

(expect RuntimeException (read-properties fp :required [:foo :int-example]))

(expect 1 (:int-example (read-properties fp :parse-int [:int-example])))

(expect NumberFormatException
        (:string-example (read-properties fp :parse-int [:string-example])))

(expect true?
        (:boolean-example (read-properties fp :parse-boolean [:boolean-example])))

(expect false?
        (:string-example (read-properties fp :parse-boolean [:string-example])))
