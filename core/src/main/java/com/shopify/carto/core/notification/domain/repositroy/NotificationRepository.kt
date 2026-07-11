package com.shopify.carto.core.notification.domain.repositroy

import com.shopify.carto.core.notification.domain.model.NotificationMessage

interface NotificationRepository {
    fun showSimpleNotification(notificationMessage: NotificationMessage)
}
