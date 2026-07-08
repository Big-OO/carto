package com.shopify.carto.feature.home.domain.usecase

import com.shopify.carto.feature.home.domain.model.Coupon
import com.shopify.carto.feature.home.domain.repository.HomeRepository
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class GetActiveCouponsUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(
        fetchLimit: Int = FETCH_LIMIT,
        displayLimit: Int = DISPLAY_LIMIT,
    ): Result<List<Coupon>> {
        return repository.getCoupons(fetchLimit)
            .map { coupons ->
                val now = System.currentTimeMillis()

                coupons
                    .filter { it.isActive(now) }
                    .shuffled()
                    .take(displayLimit)
            }
    }

    private fun Coupon.isActive(nowMillis: Long): Boolean {
        val startMillis = startsAt?.toShopifyEpochMillisOrNull()
        val endMillis = endsAt?.toShopifyEpochMillisOrNull()

        val hasValidCode = code.isNotBlank()
        val hasStarted = startMillis == null || startMillis <= nowMillis
        val hasNotEnded = endMillis == null || endMillis > nowMillis

        return hasValidCode && hasStarted && hasNotEnded
    }

    private fun String.toShopifyEpochMillisOrNull(): Long? {
        return runCatching {
            SimpleDateFormat(SHOPIFY_DATE_PATTERN, Locale.US).parse(this)?.time
        }.getOrNull()
    }

    private companion object {
        const val FETCH_LIMIT = 50
        const val DISPLAY_LIMIT = 3
        const val SHOPIFY_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX"
    }
}
