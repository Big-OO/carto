package com.shopify.carto.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mapbox.common.MapboxOptions
import com.shopify.carto.BuildConfig
import com.shopify.carto.core.notification.domain.work.NotificationWorkerManager
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.shopify.carto.feature.currency.worker.CurrencySyncWorker


@HiltAndroidApp
class CartoApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN

        enqueueNotificationWorker()
        enqueueCurrencyWorker()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun enqueueNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorkerManager>(
                repeatInterval = 3,
                repeatIntervalTimeUnit = TimeUnit.HOURS,
            )
                .setConstraints(constraints)
                .addTag(NOTIFICATION_WORK_TAG)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWorkRequest,
        )
    }

    private fun enqueueCurrencyWorker(){
        val syncWorkRequest = PeriodicWorkRequestBuilder<CurrencySyncWorker>(
            3, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CurrencySyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    companion object {
        private const val NOTIFICATION_WORK_NAME = "notification_periodic_work"
        private const val NOTIFICATION_WORK_TAG = "notification_worker"
    }
}