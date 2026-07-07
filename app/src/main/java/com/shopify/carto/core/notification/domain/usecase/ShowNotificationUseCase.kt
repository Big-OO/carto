package com.shopify.carto.core.notification.domain.usecase

import com.shopify.carto.core.notification.domain.model.NotificationMessage
import com.shopify.carto.core.notification.domain.repositroy.NotificationRepository
import javax.inject.Inject


class ShowNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {

    operator fun invoke(notificationMessage: NotificationMessage) {
        if (notificationMessage.title.isBlank() || notificationMessage.subtitle.isBlank()) return

        repository.showSimpleNotification(notificationMessage)
    }
}
