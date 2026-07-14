package com.shopify.carto.feature.home.presentation.screens.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.feature.home.domain.model.Coupon
import kotlinx.coroutines.delay
import java.text.DecimalFormat

@Composable
fun CouponCardItem(
    coupon: Coupon,
    onCopyCodeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    couponNumber: Int,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        label = "coupon_press_scale"
    )

    var isCopied by remember { mutableStateOf(false) }
    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    val buttonBgColor by animateColorAsState(
        targetValue = if (isCopied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surface,
        label = "coupon_btn_bg"
    )
    val buttonContentColor by animateColorAsState(
        targetValue = if (isCopied) Color.White else MaterialTheme.colorScheme.primary,
        label = "coupon_btn_content"
    )

    val bannerRes = when (couponNumber % 3) {
        0 -> R.drawable.banner1
        1 -> R.drawable.banner2
        else -> R.drawable.banner3
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            // Background Image
            Image(
                painter = painterResource(id = bannerRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // Dynamic gradient overlay to ensure text contrast
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.9f),
                                Color.Black.copy(alpha = 0.65f),
                                Color.Black.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            startX = 0.0f
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Details Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Modern Badge label
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SPECIAL OFFER",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = coupon.code,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = coupon.discountLabel(),
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onCopyCodeClick(coupon.code)
                            isCopied = true
                        },
                        interactionSource = interactionSource,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonBgColor,
                            contentColor = buttonContentColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Text(
                            text = if (isCopied) "Copied!" else stringResource(R.string.homeCouponCopyCode),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Vertical ticket dashed divider
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    drawLine(
                        color = Color.White.copy(alpha = 0.35f),
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = pathEffect
                    )
                }

                Spacer(Modifier.width(16.dp))
            }
        }
    }
}

@Composable
private fun Coupon.discountLabel(): String {
    val value = discountValue.formatDiscountValue()
    return if (isPercentage) {
        stringResource(R.string.homeCouponPercentageOff, value)
    } else {
        stringResource(R.string.homeCouponFixedAmountOff, value)
    }
}

private fun Coupon.shortDiscountLabel(): String {
    val value = discountValue.formatDiscountValue()
    return if (isPercentage) "$value%" else value
}

private fun Double.formatDiscountValue(): String {
    return DecimalFormat("0.##").format(this)
}
