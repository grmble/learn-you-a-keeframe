(ns grmble.lyakf.frontend.subscriptions
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub :ui get-in)
(rf/reg-sub :config get-in)
