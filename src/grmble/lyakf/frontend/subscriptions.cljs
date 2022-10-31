(ns grmble.lyakf.frontend.subscriptions
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub :ui
            (fn [db [& args]]
              (pr ":ui" args)
              (get-in db args)))
(rf/reg-sub :config
            (fn [db [& args]]
              (pr ":config" args)
              (get-in db args)))
