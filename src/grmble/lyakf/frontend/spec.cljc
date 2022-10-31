(ns grmble.lyakf.frontend.spec
  (:require
   #?(:clj  [clojure.spec.alpha :as s]
      :cljs [cljs.spec.alpha :as s])))

;; simply stubbing out the usage using goog.DEBUG will give
;; 62KB gzipped js (vs 58KB when spec.alpha is simply not used)
;;
;; using conditional reading we could get rid of any reference
;; this would give a 58KB release js, but adds more complexity
;; to the build
;;
;; https://shadow-cljs.github.io/docs/UsersGuide.html#_conditional_reading

(when ^boolean goog.DEBUG
  (s/def ::db-spec (s/keys :req-un [::ui
                                    ::config
                                    ::training-programs]))

  (s/def ::ui (s/keys :req-un [::initialized?]))
  (s/def ::initialized? boolean?)

  (s/def ::config (s/keys :req-un [::show-dev-tab?]))
  (s/def ::show-dev-tab? boolean?)


  (s/def ::training-programs (s/map-of ::program-name ::training-program))
  (s/def ::training-program (s/keys :req-un [::program-name
                                             ::exercises]))
  (s/def ::program-name string?)

  (s/def ::exercises (s/coll-of ::exercise))
  (s/def ::exercise (s/or ::progression ::progression
                          ::alternating ::alternating))

  (defmulti progression-type :type)
  (defmethod progression-type :linear [_]
    (s/keys :req-un [::exercise-slug
                     ::weight
                     ::increment
                     ::round-to-multiples-of]))
  (s/def ::progression (s/multi-spec progression-type :type))
  (s/def ::alternating (s/coll-of ::progression))


  (s/def ::exercise-slug string?)
  (s/def ::weight number?)
  (s/def ::increment number?)
  (s/def ::round-to-multiples-of number?))

(defn validate-db [db _]
  (when ^boolean goog.DEBUG
    (if (s/valid? ::db-spec db)
      db
      (s/explain ::db-spec db))))
