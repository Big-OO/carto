package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopify.carto.R

@Composable
fun MarkdownRenderer(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val segments = remember(text) { parseMessageSegments(text) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        segments.forEach { segment ->
            when (segment) {
                is ChatSegment.Table     -> ComparisonTable(headers = segment.headers, rows = segment.rows)
                is ChatSegment.CodeBlock -> CodeBlock(language = segment.language, code = segment.code)
                is ChatSegment.Text     -> TextSegmentRenderer(text = segment.content, color = color)
            }
        }
    }
}

@Composable
fun TextSegmentRenderer(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val lines = text.split("\n")
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isBlank()) return@forEach

            when {
                trimmed == "---" || trimmed == "***" -> HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = color.copy(alpha = 0.25f)
                )
                trimmed.startsWith("### ") -> Text(
                    text = trimmed.removePrefix("### "),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    modifier = Modifier.padding(top = 4.dp)
                )
                trimmed.startsWith("## ") -> Text(
                    text = trimmed.removePrefix("## "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 6.dp)
                )
                trimmed.startsWith("# ") -> Text(
                    text = trimmed.removePrefix("# "),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    modifier = Modifier.padding(top = 8.dp)
                )
                trimmed.startsWith("> ") -> Surface(
                    color = color.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(20.dp)
                                .background(color.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = buildInlineStyledText(trimmed.removePrefix("> "), color),
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = color.copy(alpha = 0.85f)
                        )
                    }
                }
                trimmed.matches(Regex("^[-*] .+")) -> Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "•", color = color.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 1.dp))
                    Text(
                        text = buildInlineStyledText(trimmed.removePrefix("- ").removePrefix("* "), color),
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                }
                trimmed.matches(Regex("^\\d+\\. .+")) -> {
                    val dotIdx = trimmed.indexOf(". ")
                    val num = trimmed.substring(0, dotIdx)
                    val content = trimmed.substring(dotIdx + 2)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "$num.", color = color.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.width(22.dp))
                        Text(
                            text = buildInlineStyledText(content, color),
                            style = MaterialTheme.typography.bodyMedium,
                            color = color
                        )
                    }
                }
                else -> Text(
                    text = buildInlineStyledText(trimmed, color),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
        }
    }
}

private fun buildInlineStyledText(text: String, defaultColor: Color): AnnotatedString {
    val cleaned = text
        .replace(Regex("^#{1,6}\\s+"), "")
        .trim()

    return buildAnnotatedString {
        val pattern = Regex("""\*\*(.+?)\*\*|\*(.+?)\*|`(.+?)`""")
        var cursor = 0
        pattern.findAll(cleaned).forEach { match ->
            val range = match.range
            if (cursor < range.first) append(cleaned.substring(cursor, range.first))
            val matchValue = match.value
            when {
                matchValue.startsWith("**") -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(match.groupValues[1])
                }
                matchValue.startsWith("`") -> withStyle(
                    SpanStyle(fontFamily = FontFamily.Monospace, background = defaultColor.copy(alpha = 0.12f))
                ) {
                    append(match.groupValues[3])
                }
                else -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(match.groupValues[2])
                }
            }
            cursor = range.last + 1
        }
        if (cursor < cleaned.length) append(cleaned.substring(cursor))
    }
}

sealed class ChatSegment {
    data class Text(val content: String) : ChatSegment()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : ChatSegment()
    data class CodeBlock(val language: String, val code: String) : ChatSegment()
}

fun parseMessageSegments(text: String): List<ChatSegment> {
    val segments = mutableListOf<ChatSegment>()
    val lines = text.split("\n")
    var mode = "TEXT"
    val currentTableLines = mutableListOf<String>()
    val currentTextLines = mutableListOf<String>()
    val currentCodeLines = mutableListOf<String>()
    var codeLanguage = ""

    fun flushText() {
        if (currentTextLines.isNotEmpty()) {
            segments.add(ChatSegment.Text(currentTextLines.joinToString("\n")))
            currentTextLines.clear()
        }
    }

    fun flushTable() {
        if (currentTableLines.isNotEmpty()) {
            val tableLines = currentTableLines.toList()
            currentTableLines.clear()
            val parsedHeaders = mutableListOf<String>()
            val parsedRows = mutableListOf<List<String>>()
            tableLines.forEachIndexed { index, line ->
                val cells = line.split("|")
                    .map { it.trim() }
                    .filterIndexed { i, _ -> i > 0 && i < line.split("|").lastIndex }
                if (index == 0) parsedHeaders.addAll(cells)
                else if (!line.contains("---")) parsedRows.add(cells)
            }
            if (parsedHeaders.isNotEmpty()) segments.add(ChatSegment.Table(parsedHeaders, parsedRows))
            else segments.add(ChatSegment.Text(tableLines.joinToString("\n")))
        }
    }

    fun flushCode() {
        if (currentCodeLines.isNotEmpty()) {
            segments.add(ChatSegment.CodeBlock(codeLanguage, currentCodeLines.joinToString("\n")))
            currentCodeLines.clear()
            codeLanguage = ""
        }
    }

    for (line in lines) {
        val trimmed = line.trim()
        val isCodeBoundary = trimmed.startsWith("```")
        val isTableLine = trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.count { it == '|' } >= 2

        when (mode) {
            "CODE" -> if (isCodeBoundary) { flushCode(); mode = "TEXT" } else currentCodeLines.add(line)
            "TABLE" -> if (isTableLine) currentTableLines.add(line)
                       else { flushTable(); if (isCodeBoundary) { codeLanguage = trimmed.substring(3).trim(); mode = "CODE" } else { currentTextLines.add(line); mode = "TEXT" } }
            else -> when {
                isCodeBoundary -> { flushText(); codeLanguage = trimmed.substring(3).trim(); mode = "CODE" }
                isTableLine    -> { flushText(); currentTableLines.add(line); mode = "TABLE" }
                else           -> currentTextLines.add(line)
            }
        }
    }
    when (mode) {
        "CODE"  -> flushCode()
        "TABLE" -> flushTable()
        else    -> flushText()
    }
    return segments
}
