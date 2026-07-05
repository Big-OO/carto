package com.shopify.carto.feature.product_reviews.data.data_source


import com.shopify.carto.feature.product_reviews.data.dto.ProductReviewsDto
import com.shopify.carto.feature.product_reviews.data.dto.ProductReviewsResponseDto
import com.shopify.carto.feature.product_reviews.data.service.ProductReviewsService
import javax.inject.Inject

class ProductReviewsRemoteDataSourceImpl @Inject constructor(
    private val service: ProductReviewsService
) : ProductReviewsRemoteDataSource {

    override suspend fun getProductReviews(productId: Long): Result<ProductReviewsResponseDto> {
        val mockResponse = ProductReviewsResponseDto(
            metafields = listOf(
                ProductReviewsDto(
                    id = 1001L,
                    namespace = "reviews",
                    key = "review_user_1",
                    value = "{\"rating\": 5, \"title\": \"Exceptional quality and build!\", \"body\": \"I was blown away by the craftsmanship. The materials feel premium, and it performs even better than advertised. Definitely worth every penny!\"}",
                    createdAt = "2026-06-25T14:22:10Z"
                ),
                ProductReviewsDto(
                    id = 1002L,
                    namespace = "reviews",
                    key = "review_user_2",
                    value = "{\"rating\": 4, \"title\": \"Great item, minor packaging damage\", \"body\": \"The product itself works flawlessly and matches the pictures exactly. My only complaint is that the outer shipping box arrived slightly crushed, but thankfully the item was intact.\"}",
                    createdAt = "2026-06-27T09:15:00Z"
                ),
                ProductReviewsDto(
                    id = 1003L,
                    namespace = "reviews",
                    key = "review_user_3",
                    value = "{\"rating\": 5, \"title\": \"Exceeded all expectations\", \"body\": \"I bought this as a gift, and they absolutely loved it. The setup process was super intuitive, and it worked right out of the box. Highly recommended!\"}",
                    createdAt = "2026-06-28T18:40:33Z"
                ),
                ProductReviewsDto(
                    id = 1004L,
                    namespace = "reviews",
                    key = "review_user_4",
                    value = "{\"rating\": 3, \"title\": \"Decent, but room for improvement\", \"body\": \"It does the job well enough for daily use. However, the battery drains a bit faster than expected under heavy load. Good value if caught on sale.\"}",
                    createdAt = "2026-06-29T11:05:12Z"
                ),
                ProductReviewsDto(
                    id = 1005L,
                    namespace = "reviews",
                    key = "review_user_5",
                    value = "{\"rating\": 5, \"title\": \"Best purchase this year\", \"body\": \"Ive tested several similar products from other brands, and none come close to this level of reliability. Will definitely be ordering another one soon.\"}",
                    createdAt = "2026-06-30T16:50:00Z"
                ),
                ProductReviewsDto(
                    id = 1006L,
                    namespace = "reviews",
                    key = "review_user_6",
                    value = "{\"rating\": 2, \"title\": \"Not quite what I expected\", \"body\": \"The color is slightly darker in person compared to the product photos, and it feels a bit bulky. Returning it for a different model.\"}",
                    createdAt = "2026-07-01T08:12:45Z"
                ),
                ProductReviewsDto(
                    id = 1007L,
                    namespace = "reviews",
                    key = "review_user_7",
                    value = "{\"rating\": 4, \"title\": \"Solid daily driver\", \"body\": \"Very reliable and sleek design. Customer support was also extremely helpful when I had a question regarding warranty registration.\"}",
                    createdAt = "2026-07-01T21:30:00Z"
                ),
                ProductReviewsDto(
                    id = 1008L,
                    namespace = "reviews",
                    key = "review_user_8",
                    value = "{\"rating\": 5, \"title\": \"Flawless experience\", \"body\": \"Super fast delivery and top-notch quality. Everything was packaged securely and works beautifully.\"}",
                    createdAt = "2026-07-02T13:00:19Z"
                )
            )
        )
        return Result.success(mockResponse)
    }
}