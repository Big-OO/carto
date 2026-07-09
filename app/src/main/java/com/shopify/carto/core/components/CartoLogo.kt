package com.shopify.carto.presentation.components

import android.content.Context
import android.content.res.XmlResourceParser
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import com.shopify.carto.R
import org.xmlpull.v1.XmlPullParser
import java.util.Locale
import kotlin.math.min

@Composable
fun CartoLogo(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    logoColor: Color = MaterialTheme.colorScheme.primary,
    @DrawableRes logoXmlRes: Int = R.drawable.carto_logo,
) {
    val context = LocalContext.current
    val logo = remember(logoXmlRes) {
        loadCartoLogoVector(
            context = context,
            logoXmlRes = logoXmlRes,
        )
    }

    Canvas(
        modifier = modifier
            .aspectRatio(logo.viewportWidth / logo.viewportHeight)
            .background(backgroundColor),
    ) {
        val scale = min(
            size.width / logo.viewportWidth,
            size.height / logo.viewportHeight,
        )

        val dx = (size.width - logo.viewportWidth * scale) / 2f
        val dy = (size.height - logo.viewportHeight * scale) / 2f

        withTransform({
            translate(left = dx, top = dy)
            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
        }) {
            logo.paths.forEach { item ->
                val color = when (item.role) {
                    CartoLogoPathRole.Background -> backgroundColor
                    CartoLogoPathRole.Foreground -> logoColor
                    CartoLogoPathRole.Orange -> item.fixedColor
                }

                withTransform({
                    translate(left = item.translateX, top = item.translateY)
                }) {
                    drawPath(
                        path = item.path,
                        color = color,
                    )
                }
            }
        }
    }
}

private const val ANDROID_NS = "http://schemas.android.com/apk/res/android"

private data class CartoLogoVector(
    val viewportWidth: Float,
    val viewportHeight: Float,
    val paths: List<CartoLogoPath>,
)

private data class CartoLogoPath(
    val path: Path,
    val role: CartoLogoPathRole,
    val fixedColor: Color,
    val translateX: Float,
    val translateY: Float,
)

private enum class CartoLogoPathRole {
    Background,
    Foreground,
    Orange,
}

private fun loadCartoLogoVector(
    context: Context,
    @DrawableRes logoXmlRes: Int,
): CartoLogoVector {
    val parser = context.resources.getXml(logoXmlRes)

    try {
        return parseCartoLogoVector(parser)
    } finally {
        parser.close()
    }
}

private fun parseCartoLogoVector(parser: XmlResourceParser): CartoLogoVector {
    var viewportWidth = 1568f
    var viewportHeight = 1003f

    val parsedPaths = mutableListOf<CartoLogoPath>()
    val translateStack = ArrayDeque<Pair<Float, Float>>()
    translateStack.addLast(0f to 0f)

    var event = parser.eventType

    while (event != XmlPullParser.END_DOCUMENT) {
        when (event) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "vector" -> {
                        viewportWidth = parser.androidFloat("viewportWidth") ?: viewportWidth
                        viewportHeight = parser.androidFloat("viewportHeight") ?: viewportHeight
                    }

                    "group" -> {
                        val parent = translateStack.last()
                        val tx = parser.androidFloat("translateX") ?: 0f
                        val ty = parser.androidFloat("translateY") ?: 0f
                        translateStack.addLast(
                            (parent.first + tx) to (parent.second + ty),
                        )
                    }

                    "path" -> {
                        val name = parser.androidString("name").orEmpty()
                        val pathData = parser.androidString("pathData").orEmpty()
                        val fillColor = parser.androidString("fillColor")

                        if (pathData.isNotBlank()) {
                            val translate = translateStack.last()
                            val role = when {
                                name.startsWith("carto_background") -> CartoLogoPathRole.Background
                                name.startsWith("carto_orange") -> CartoLogoPathRole.Orange
                                else -> CartoLogoPathRole.Foreground
                            }

                            parsedPaths += CartoLogoPath(
                                path = PathParser()
                                    .parsePathString(pathData)
                                    .toPath(),
                                role = role,
                                fixedColor = fillColor.toComposeColorOrDefault(CartoOrange),
                                translateX = translate.first,
                                translateY = translate.second,
                            )
                        }
                    }
                }
            }

            XmlPullParser.END_TAG -> {
                if (parser.name == "group" && translateStack.size > 1) {
                    translateStack.removeLast()
                }
            }
        }

        event = parser.next()
    }

    return CartoLogoVector(
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
        paths = parsedPaths,
    )
}

private fun XmlResourceParser.androidString(name: String): String? {
    return getAttributeValue(ANDROID_NS, name)
}

private fun XmlResourceParser.androidFloat(name: String): Float? {
    return androidString(name)?.removeSuffix("dp")?.toFloatOrNull()
}

private val CartoOrange = Color(0xFFFB5B08)

private fun String?.toComposeColorOrDefault(default: Color): Color {
    if (this == null || !startsWith("#")) return default

    val hex = removePrefix("#").uppercase(Locale.US)

    return when (hex.length) {
        6 -> Color((0xFF000000L or hex.toLong(16)).toULong())
        8 -> Color(hex.toLong(16).toULong())
        else -> default
    }
}
