(ns grmble.lyakf.frontend.sub
  (:require
   [grmble.lyakf.frontend.model.program :as program]
   [re-frame.core :as rf]))

;; (fn [db [_ & args]] (get-in db args))
;; (fn [db args] (get-in args))
(rf/reg-sub :ui get-in)
(rf/reg-sub :config get-in)
(rf/reg-sub :current get-in)

;; (:exercises {:type 42} "BOOM") ==> BOOM
;; (fn [db _] (:exercises db))
(rf/reg-sub :exercises :-> :exercises)
(rf/reg-sub :programs :-> :programs)


;; (<sub [:sorted-programs :name])
(rf/reg-sub :sorted-programs
            (fn [_qv]
              [(rf/subscribe [:programs])
               (rf/subscribe [:current :slug])])
            (fn [[programs slug] [_ key]]
              (->> programs
                   (vals)
                   (map (fn [{pslug :slug :as p}] (assoc p :current? (= pslug slug))))
                   (sort-by key))))

(rf/reg-sub :current-program
            (fn [_qv]
              [(rf/subscribe [:programs])
               (rf/subscribe [:current :slug])])
            (fn [[programs slug] _]
              (programs slug)))

;; selectors for the current workout
;; GOD I LOVE RE-FRAME
;; i put this is the transient part of app db,
;; but it was janky because of all the update logic,
;; and initalisation would be hell once local storage is in place.
;; but it does not need to be in the model at all!
;; it is a function of the current program and the current
;; program data ...
(rf/reg-sub :workout-selectors
            (fn [_qv]
              [(rf/subscribe [:current-program])
               (rf/subscribe [:current :data])])
            (fn [[program data] _]
              (program/current-selectors program data)))

(rf/reg-sub :current-workout-info
            (fn [_qv]
              [(rf/subscribe [:current-program])
               (rf/subscribe [:exercises])
               (rf/subscribe [:workout-selectors])
               (rf/subscribe [:current :data])])
            (fn [[program exercises selectors data] _]
              (let [completed?     (program/mk-completed? data)
                    uncompleted    (remove completed? selectors)]
                (mapv (fn [sel]
                        (let [completed (completed? sel)
                              xref      (program/exercise-ref program sel)
                              exercise  (->> xref
                                             :slug
                                             exercises)]
                          {:exercise exercise
                           :selector sel
                           :repsets completed
                           ;; focus seems to go to the LAST element with auto-focus
                           :focus (first uncompleted)
                           :suggestion (program/wizard-suggestion xref exercises)}))
                      selectors))))
