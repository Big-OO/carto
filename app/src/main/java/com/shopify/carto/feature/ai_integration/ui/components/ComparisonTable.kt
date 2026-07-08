package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ComparisonTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header row
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    headers.forEach { header ->
                        Text(
                            text = header,
                            modifier = Modifier.widthIn(min = 80.dp, max = 150.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                rows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 7.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { cell ->
                            Text(
                                text = cell,
                                modifier = Modifier.widthIn(min = 80.dp, max = 150.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
