(ns grmble.lyakf.frontend.model.parser
  "Parser for log entries / rep sets"
  (:require
   #?(:cljs [instaparse.core :as insta :refer-macros [defparser]]
      :clj [instaparse.core :as insta :refer [defparser]])))

(time
 (defparser field-parser "entry = (annotation <ws>)* date <ws> slug <ws> repsets <ws?>;
        date = #'\\d{4}-\\d{2}-\\d{2}';
        slug = #'[-\\w]+';
        repsets = repset (<ws> repset)*;
        repset = (annotation <ws>)* weight [<ws?> <('x'|'*')> reps [<ws?> <('x'|'*')> sets]];
        ws = #'\\s+';
        weight = #'(\\d+)([\\.,]\\d*)?';
        reps = #'\\d+';
        sets = #'\\d+';
        annotation = <'@'> #'\\w+';
        " :start :repsets))


(defn parse-field [s]
  (insta/parse field-parser s))

(defn field-invalid? [s]
  (-> (insta/parse field-parser s)
      (insta/failure?)))


