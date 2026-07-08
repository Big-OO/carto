package com.shopify.carto.feature.ai_integration.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CodeBlock(
    language: String,
    code: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16213E))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "code" }.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7BC8F6),
                    fontWeight = FontWeight.Bold
                )
                val context = LocalContext.current
                val clipboard = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
                Text(
                    text = "Copy",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4FC3F7),
                    modifier = Modifier
                        .clickable { clipboard.setPrimaryClip(ClipData.newPlainText("Code", code)) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Text(
                text = code,
                modifier = Modifier.fillMaxWidth().padding(12.dp).horizontalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFD4E6F1)
                )
            )
        }
    }
}
