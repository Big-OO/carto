package com.shopify.carto.feature.ai_widget.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
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
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.shopify.carto.MainActivity
import com.shopify.carto.R

class CartoAiGlanceWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val strings = CartoAiWidgetStrings.from(context)
        provideContent {
            CartoAiWidgetContent(strings = strings)
        }
    }
}

@Composable
private fun CartoAiWidgetContent(strings: CartoAiWidgetStrings) {
    val context = LocalContext.current

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(DarkBackground)
            .cornerRadius(26.dp)
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AskCartoAiButton(
                strings = strings,
                modifier = GlanceModifier.defaultWeight(),
                onClick = actionStartActivity(createOpenAiIntent(context = context, startVoice = false)),
            )

            Spacer(modifier = GlanceModifier.width(10.dp))

            RecordButton(
                label = strings.recordLabel,
                onClick = actionStartActivity(createOpenAiIntent(context = context, startVoice = true)),
            )
        }
    }
}

@Composable
private fun AskCartoAiButton(
    strings: CartoAiWidgetStrings,
    modifier: GlanceModifier = GlanceModifier,
    onClick: Action,
) {
    Row(
        modifier = modifier
            .height(82.dp)
            .background(CardBackground)
            .cornerRadius(20.dp)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .clickable(onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = GlanceModifier
                .size(44.dp)
                .background(WhiteTint)
                .cornerRadius(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(R.mipmap.ic_launcher_round),
                contentDescription = strings.askCartoAiLabel,
                modifier = GlanceModifier.size(28.dp),
            )
        }

        Spacer(modifier = GlanceModifier.width(12.dp))

        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = strings.askCartoAiLabel,
                maxLines = 1,
                style = TextStyle(
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = strings.askCartoAiSubtitle,
                maxLines = 1,
                style = TextStyle(
                    color = MutedText,
                    fontSize = 11.sp,
                ),
            )
        }
    }
}

@Composable
private fun RecordButton(
    label: String,
    onClick: Action,
) {
    Column(
        modifier = GlanceModifier
            .width(86.dp)
            .height(82.dp)
            .background(CardBackground)
            .cornerRadius(20.dp)
            .padding(vertical = 12.dp)
            .clickable(onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = GlanceModifier
                .size(40.dp)
                .background(Primary)
                .cornerRadius(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_widget_mic),
                contentDescription = label,
                modifier = GlanceModifier.size(20.dp),
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Text(
            text = label,
            maxLines = 1,
            style = TextStyle(
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

private fun createOpenAiIntent(
    context: Context,
    startVoice: Boolean,
): Intent = Intent(context, MainActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    putExtra(MainActivity.EXTRA_OPEN_AI_ASSISTANT, true)
    putExtra(MainActivity.EXTRA_START_AI_VOICE, startVoice)
}

private data class CartoAiWidgetStrings(
    val askCartoAiLabel: String,
    val askCartoAiSubtitle: String,
    val recordLabel: String,
) {
    companion object {
        fun from(context: Context) = CartoAiWidgetStrings(
            askCartoAiLabel = context.getString(R.string.carto_ai_widget_title),
            askCartoAiSubtitle = context.getString(R.string.carto_ai_widget_subtitle),
            recordLabel = context.getString(R.string.carto_ai_widget_record),
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
private val WhiteTint = ColorProvider(Color(0xFFEEF1FF))
@SuppressLint("RestrictedApi")
private val MutedText = ColorProvider(Color(0xFFB7BED4))
