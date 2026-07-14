package com.shopify.carto.core.notification.domain.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shopify.carto.R
import com.shopify.carto.core.notification.domain.model.NotificationMessage
import com.shopify.carto.core.notification.domain.usecase.ShowNotificationUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.ObserveCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.RefreshCartUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random

@HiltWorker
class NotificationWorkerManager @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val notificationUseCase: ShowNotificationUseCase,
    private val refreshCartUseCase: RefreshCartUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!hasNotificationPermission()) {
            return Result.success()
        }

        return runCatching {
            withContext(Dispatchers.IO) {
                refreshCartUseCase()
                val cartIsEmpty = isCartEmpty()

                val notificationMessage = if (cartIsEmpty) {
                    getRandomGeneralNotification()
                } else {
                    getCartNotification()
                }

                notificationUseCase(notificationMessage)
            }

            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun isCartEmpty(): Boolean {
        return withTimeoutOrNull(5_000) {
            observeCartUseCase()
                .map { result ->
                    result.getOrNull()
                        ?.lines
                        .orEmpty()
                        .isEmpty()
                }
                .first()
        } ?: true
    }

    private fun getRandomGeneralNotification(): NotificationMessage {
        val titles = context.resources.getStringArray(
            R.array.notification_general_titles,
        )

        val subtitles = context.resources.getStringArray(
            R.array.notification_general_subtitles,
        )

        val itemCount = minOf(titles.size, subtitles.size)

        if (itemCount == 0) {
            return NotificationMessage(
                title = context.getString(R.string.notification_default_title),
                subtitle = context.getString(R.string.notification_default_subtitle),
            )
        }

        val randomIndex = Random.nextInt(itemCount)
        return NotificationMessage(
            title = titles[randomIndex],
            subtitle = subtitles[randomIndex],
        )
    }

    private fun getCartNotification(): NotificationMessage {
        return NotificationMessage(
            title = context.getString(R.string.notification_cart_title),
            subtitle = context.getString(R.string.notification_cart_subtitle),
        )
    }
}