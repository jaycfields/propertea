# propertea

> simple property loading, coercing, and validating

## An Example

propertea can be used to load a property file, convert, and
validate. The following snippet shows loading a file and converting a
few of the properties to their desired types.
<pre>
;Given the following properties file
;string-example=hello-string
;int-example=1
;boolean-example=true

(ns example 
  (:use propertea.core))         

(def props (read-properties "test/fake.properties" 
                            :parse-int [:int-example] 
                            :parse-boolean [:boolean-example]))

(println props) 
; => {:int-example 1, :string-example "hello-string", :boolean-example true}
</pre>
propertea can also validate that required properties are specified.
<pre>
; assuming the same properties file as above

(ns example 
  (:use propertea.core))         

(def props (read-properties "test/fake.properties" :required [:req-prop])) 
; => java.lang.RuntimeException: (:req-prop) are required, but not found
</pre>

## Installing

The easiest way is via Leiningen. Add the following dependency to your project.clj file:<pre>[propertea "1.0.0"]</pre>
To build from source, run the following commands:<pre>lein deps
lein jar</pre>

## License

Copyright (c) 2010, Jay Fields
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

* Neither the name Jay Fields nor the names of the contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
