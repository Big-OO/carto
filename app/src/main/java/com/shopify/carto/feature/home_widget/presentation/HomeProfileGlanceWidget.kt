package com.shopify.carto.feature.home_widget.presentation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.shopify.carto.R
import com.shopify.carto.feature.home_widget.domain.model.HomeProfileWidgetData
import com.shopify.carto.feature.home_widget.domain.model.HomeProfileWidgetState
import dagger.hilt.android.EntryPointAccessors

class HomeProfileGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = loadWidgetState(context)
        val strings = HomeProfileWidgetStrings.from(context)

        provideContent {
            HomeProfileWidgetContent(
                state = state,
                strings = strings,
            )
        }
    }

    private suspend fun loadWidgetState(context: Context): HomeProfileWidgetState {
        return runCatching {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                HomeProfileWidgetEntryPoint::class.java,
            )
            entryPoint.getHomeProfileWidgetDataUseCase().invoke()
        }.getOrElse {
            HomeProfileWidgetState.Unavailable
        }
    }
}

@Composable
private fun HomeProfileWidgetContent(
    state: HomeProfileWidgetState,
    strings: HomeProfileWidgetStrings,
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(DarkBackground)
            .cornerRadius(26.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            HomeProfileWidgetState.Guest -> WidgetMessageContent(
                title = strings.guestTitle,
                message = strings.guestMessage,
            )

            HomeProfileWidgetState.Unavailable -> WidgetMessageContent(
                title = strings.unavailableTitle,
                message = strings.unavailableMessage,
            )

            is HomeProfileWidgetState.Content -> CustomerProfileContent(
                data = state.data,
                strings = strings,
            )
        }
    }
}

@Composable
private fun CustomerProfileContent(
    data: HomeProfileWidgetData,
    strings: HomeProfileWidgetStrings,
) {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InitialsLogo(initials = data.initials)

            Spacer(modifier = GlanceModifier.width(12.dp))

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = data.fullName,
                    maxLines = 1,
                    style = TextStyle(
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Spacer(modifier = GlanceModifier.height(3.dp))

                Text(
                    text = strings.customerIdLabel.replace("%s", data.customerId),
                    maxLines = 1,
                    style = TextStyle(
                        color = MutedText,
                        fontSize = 12.sp,
                    ),
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(16.dp))

        Row(modifier = GlanceModifier.fillMaxWidth()) {
            StatBox(
                title = strings.ordersLabel,
                value = data.ordersCount.toString(),
                modifier = GlanceModifier.defaultWeight(),
            )

            Spacer(modifier = GlanceModifier.width(10.dp))

            StatBox(
                title = strings.totalPaidLabel,
                value = data.totalPaid,
                modifier = GlanceModifier.defaultWeight(),
            )
        }

        Spacer(modifier = GlanceModifier.defaultWeight())

        Text(
            text = strings.footerText,
            maxLines = 1,
            style = TextStyle(
                color = AccentText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun InitialsLogo(initials: String) {
    Box(
        modifier = GlanceModifier
            .size(54.dp)
            .background(Primary)
            .cornerRadius(27.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            maxLines = 1,
            style = TextStyle(
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = modifier
            .background(CardBackground)
            .cornerRadius(18.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            maxLines = 1,
            style = TextStyle(
                color = MutedText,
                fontSize = 11.sp,
            ),
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        Text(
            text = value,
            maxLines = 1,
            style = TextStyle(
                color = White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun WidgetMessageContent(
    title: String,
    message: String,
) {
    Column(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            maxLines = 1,
            style = TextStyle(
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        Text(
            text = message,
            maxLines = 2,
            style = TextStyle(
                color = MutedText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

private data class HomeProfileWidgetStrings(
    val customerIdLabel: String,
    val ordersLabel: String,
    val totalPaidLabel: String,
    val footerText: String,
    val guestTitle: String,
    val guestMessage: String,
    val unavailableTitle: String,
    val unavailableMessage: String,
) {
    companion object {
        fun from(context: Context) = HomeProfileWidgetStrings(
            customerIdLabel = context.getString(R.string.home_profile_widget_customer_id),
            ordersLabel = context.getString(R.string.home_profile_widget_orders),
            totalPaidLabel = context.getString(R.string.home_profile_widget_total_paid),
            footerText = context.getString(R.string.home_profile_widget_footer),
            guestTitle = context.getString(R.string.home_profile_widget_guest_title),
            guestMessage = context.getString(R.string.home_profile_widget_guest_message),
            unavailableTitle = context.getString(R.string.home_profile_widget_unavailable_title),
            unavailableMessage = context.getString(R.string.home_profile_widget_unavailable_message),
        )
    }
}

@SuppressLint("RestrictedApi")
private val DarkBackground = ColorProvider(Color(0xFF12151F))
@SuppressLint("RestrictedApi")
private val CardBackground = ColorProvider(Color(0xFF202638))
@SuppressLint("RestrictedApi")
private val Primary = ColorProvider(Color(0xFF6C5CE7))
@SuppressLint("RestrictedApi")
private val White = ColorProvider(Color(0xFFFFFFFF))
@SuppressLint("RestrictedApi")
private val MutedText = ColorProvider(Color(0xFFB7BED4))
@SuppressLint("RestrictedApi")
private val AccentText = ColorProvider(Color(0xFF9BE7C5))
