package com.shopify.carto.feature.currency.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shopify.carto.feature.currency.domain.usecase.RefreshExchangeRatesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CurrencySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshExchangeRatesUseCase: RefreshExchangeRatesUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val result = refreshExchangeRatesUseCase()
        return if (result.isSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
