package com.shopify.carto.feature.product_reviews.data.mapper

import com.shopify.carto.feature.product_reviews.data.dto.ProductReviewsDto
import com.shopify.carto.feature.product_reviews.data.dto.ReviewValueDto
import com.shopify.carto.feature.product_reviews.domain.exception.ProductReviewsException
import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun List<ProductReviewsDto>.toDomain(gson: Gson):ProductReviews {
    val reviews = this.mapNotNull { metafield ->
        try {
            val reviewValue = gson.fromJson(metafield.value, ReviewValueDto::class.java)
            ProductReviews.ProductReview(
                id = metafield.id,
                rating = reviewValue.rating,
                title = reviewValue.title,
                body = reviewValue.body,
                author = "Verified Buyer",
                date = metafield.createdAt.formatTimeAgo()
            )
        } catch (e: Exception) {
            null
        }
    }

    val totalReviews = reviews.size
    val averageRating = if (totalReviews > 0) {
        (reviews.sumOf { it.rating }.toDouble() / totalReviews).let {
            (it * 10.0).roundToInt() / 10.0
        }
    } else 0.0

    val distribution = mutableMapOf(5 to 0f, 4 to 0f, 3 to 0f, 2 to 0f, 1 to 0f)
    if (totalReviews > 0) {
        val counts = reviews.groupingBy { it.rating }.eachCount()
        counts.forEach { (rating, count) ->
            distribution[rating] = count.toFloat() / totalReviews
        }
    }

    return ProductReviews(
        summary = ProductReviews.ProductReviewsSummary(
            averageRating = averageRating,
            totalReviews = totalReviews,
            ratingDistribution = distribution
        ),
        reviews = reviews
    )
}

private fun String.formatTimeAgo(): String {
    return try {
        val normalizedString = if (this.matches(Regex(".*[+-]\\d{2}:\\d{2}$"))) {
            this.substring(0, this.length - 3) + this.substring(this.length - 2)
        } else {
            this
        }
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        val date = format.parse(normalizedString) ?: return this
        val now = System.currentTimeMillis()
        val diffInMillis = now - date.time
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis).coerceAtLeast(0)
        val weeks = days / 7
        when {
            days == 0L -> "Today"
            days == 1L -> "1 day ago"
            days < 7L -> "$days days ago"
            weeks == 1L -> "1 week ago"
            else -> "$weeks weeks ago"
        }
    } catch (e: Exception) {
        this
    }
}

fun Throwable.toDomainException(productId: Long): ProductReviewsException {
    return when (this) {
        is HttpException -> when (code()) {
            404 -> ProductReviewsException.NotFound(productId)
            401, 403 -> ProductReviewsException.Unauthorized()
            else -> ProductReviewsException.Unknown(this)
        }
        is IOException -> ProductReviewsException.Network(this)
        else -> ProductReviewsException.Unknown(this)
    }
}