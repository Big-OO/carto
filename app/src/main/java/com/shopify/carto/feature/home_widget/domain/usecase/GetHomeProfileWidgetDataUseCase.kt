package com.shopify.carto.feature.home_widget.domain.usecase

import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.feature.home_widget.domain.model.HomeProfileWidgetData
import com.shopify.carto.feature.home_widget.domain.model.HomeProfileWidgetState
import com.shopify.carto.feature.profile.domain.model.CustomerProfile
import com.shopify.carto.feature.profile.domain.usecase.ObserveProfileUseCase
import com.shopify.carto.feature.profile.domain.usecase.RefreshProfileUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetHomeProfileWidgetDataUseCase @Inject constructor(
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val observeProfileUseCase: ObserveProfileUseCase,
    private val refreshProfileUseCase: RefreshProfileUseCase,
) {
    suspend operator fun invoke(): HomeProfileWidgetState {
        val session = observeAppSessionUseCase().first()
        val customerId = session.customerId?.toLongOrNull()

        if (!session.isLoggedIn || session.isGuest || customerId == null) {
            return HomeProfileWidgetState.Guest
        }

        val cachedProfile = observeProfileUseCase(customerId).first()
        if (cachedProfile == null) {
            refreshProfileUseCase(customerId)
        } else {
            refreshProfileUseCase(customerId)
                .onFailure { return HomeProfileWidgetState.Content(cachedProfile.toWidgetData()) }
        }

        val latestProfile = observeProfileUseCase(customerId).first()
        return latestProfile?.let { HomeProfileWidgetState.Content(it.toWidgetData()) }
            ?: HomeProfileWidgetState.Unavailable
    }

    private fun CustomerProfile.toWidgetData(): HomeProfileWidgetData {
        val fullName = listOf(firstName, lastName)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(separator = " ")
            .ifBlank { email.substringBefore('@').ifBlank { id } }

        return HomeProfileWidgetData(
            customerId = id,
            fullName = fullName,
            initials = fullName.toInitials(),
            ordersCount = ordersCount,
            totalPaid = totalSpent.toCurrencyText(),
        )
    }

    private fun String.toInitials(): String {
        val parts = trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        return parts
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
            .joinToString(separator = "")
            .ifBlank { "C" }
    }

    private fun String.toCurrencyText(): String {
        val value = trim()
        if (value.isBlank()) return "\$0.00"
        return when {
            value.startsWith("\$") -> value
            value.contains("EGP", ignoreCase = true) -> value
            value.contains("USD", ignoreCase = true) -> value
            else -> "\$$value"
        }
    }
}
