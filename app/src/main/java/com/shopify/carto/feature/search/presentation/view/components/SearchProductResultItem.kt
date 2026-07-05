package com.shopify.carto.feature.search.presentation.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.R
import com.shopify.carto.feature.search.domain.model.SearchProduct
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun SearchProductResultItem(
    product: SearchProduct,
    onClick: () -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 20.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NetworkProductImage(
                imageUrl = product.imageUrl,
                contentDescription = product.title,
                size = 76.dp,
            )

            Spacer(Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = product.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = product.priceWithDiscountLabel(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_open_arrow),
                    contentDescription = null,
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun SearchProduct.priceWithDiscountLabel() = buildAnnotatedString {
    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
        append("$ ")
        append(price.formatPrice())
    }

    val discount = discountPercentage()
    if (discount != null) {
        append(" ")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
            append("-")
            append(discount.toString())
            append("%")
        }
    }
}

private fun SearchProduct.discountPercentage(): Int? {
    val oldPrice = compareAtPrice ?: return null
    if (oldPrice <= price || oldPrice <= 0.0) {
        return null
    }
    return (((oldPrice - price) / oldPrice) * 100).roundToInt()
}

private fun Double.formatPrice(): String {
    return DecimalFormat("#,###.##").format(this)
}
