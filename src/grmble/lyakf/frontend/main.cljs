(ns grmble.lyakf.frontend.main
  (:require
   [grmble.lyakf.frontend.spec :as spec]
   [grmble.lyakf.frontend.subscriptions]
   [grmble.lyakf.frontend.view.page :as page]
   [grmble.lyakf.frontend.util :refer [<sub]]
   [reagent.dom :as rdom]
   [re-frame.core :as rf]))

;; println prints to the browser console
(enable-console-print!)

(def initial-db
  {:ui {:initialized? true
        :current-tab :home}
   :config {:show-dev-tab? true}
   :training-programs {}})

(rf/reg-event-db :initial-db
                 (fn [_ [_ db]] db))

(rf/reg-event-db :set-current-tab
                 (fn [db [_ tab]]
                   (assoc-in db [:ui :current-tab] tab)))

(defn loader [body]
  (if (and true (<sub [:ui :initialized?]))
    body
    [page/loading-page]))

(def debug? ^boolean goog.DEBUG)
(when debug?
  ;; (rf/reg-global-interceptor rf/debug)
  (rf/reg-global-interceptor
   (rf/enrich spec/validate-db)))

;; init! is called initially by shadlow-cljs (init-fn)
;; after-load! is called after every load
(defn ^:dev/after-load after-load! []
  (rf/clear-subscription-cache!)
  (let [element (.getElementById js/document "app")]
    (rdom/unmount-component-at-node element)
    (rdom/render [loader [page/current-page]] element)))
(defn init! []
  (rf/dispatch-sync [:initial-db initial-db])
  (after-load!)
  (println "init! complete"))

