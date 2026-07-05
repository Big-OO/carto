package com.shopify.carto.feature.shopping_cart.data.mapper

import com.shopify.carto.core.graphql.shopify.GetCartQuery
import com.shopify.carto.feature.shopping_cart.domain.model.Cart
import com.shopify.carto.feature.shopping_cart.domain.model.CartLine

fun GetCartQuery.Cart.toDomain(): Cart {
    val lines = lines.edges.mapNotNull { edge ->
        val node = edge.node
        val variant = node.merchandise.onProductVariant ?: return@mapNotNull null
        CartLine(
            id = node.id,
            merchandiseId = variant.id,
            productTitle = variant.product.title,
            variantTitle = variant.title,
            imageUrl = variant.product.featuredImage?.url?.toString(),
            price = variant.price.amount.toString().toDoubleOrNull() ?: 0.0,
            quantity = node.quantity
        )
    }

    return Cart(
        id = id,
        checkoutUrl = checkoutUrl.toString(),
        lines = lines,
        subtotal = cost.subtotalAmount.amount.toString().toDoubleOrNull() ?: 0.0,
        total = cost.totalAmount.amount.toString().toDoubleOrNull() ?: 0.0,
        currency = cost.totalAmount.currencyCode.toString(),
        totalQuantity = totalQuantity
    )
}