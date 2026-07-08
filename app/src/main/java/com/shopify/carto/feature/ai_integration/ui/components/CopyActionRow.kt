package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CopyActionRow(
    showRegenerate: Boolean,
    onCopyClick: () -> Unit,
    onRegenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 2.dp, start = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCopyClick, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(13.dp)
            )
        }
        if (showRegenerate) {
            IconButton(onClick = onRegenerateClick, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Regenerate",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
