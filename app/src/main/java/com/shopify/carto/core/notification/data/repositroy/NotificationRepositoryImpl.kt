package com.shopify.carto.core.notification.data.repositroy

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shopify.carto.R
import com.shopify.carto.core.notification.domain.model.NotificationMessage
import com.shopify.carto.core.notification.domain.repositroy.NotificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NotificationRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : NotificationRepository {

    private val channelId = "carto_high_priority_notifications_channel"
    private val channelName = "Carto Notifications"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Carto shopping reminders and offers"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    override fun showSimpleNotification(notificationMessage: NotificationMessage) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationMessage.title)
            .setContentText(notificationMessage.subtitle)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationMessage.subtitle),
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .setContentIntent(createOpenAppPendingIntent())
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(),
            notification,
        )
    }

    private fun createOpenAppPendingIntent(): PendingIntent {
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        return PendingIntent.getActivity(
            context,
            OPEN_APP_REQUEST_CODE,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val OPEN_APP_REQUEST_CODE = 1001
    }
}