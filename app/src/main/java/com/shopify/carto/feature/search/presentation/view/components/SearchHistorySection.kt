package com.shopify.carto.feature.search.presentation.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.feature.search.domain.model.SearchHistoryItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHistorySection(
    history: List<SearchHistoryItem>,
    onHistoryItemClicked: (String) -> Unit,
    onHistoryItemDeleted: (Long) -> Unit,
    onClearHistoryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (history.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        Text(
            text = "Recent searches",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        FlowRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3,
        ) {
            history.forEach { item ->
                SearchHistoryChip(
                    item = item,
                    onClick = { onHistoryItemClicked(item.query) },
                    onDeleteClick = { onHistoryItemDeleted(item.id) },
                )
            }
        }
    }
}

@Composable
private fun SearchHistoryChip(
    item: SearchHistoryItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 9.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HistoryClockIcon(
                modifier = Modifier.size(17.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.width(7.dp))

            Text(
                text = item.query,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )

            Spacer(Modifier.width(4.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(26.dp),
            ) {
                ChipCloseIcon(
                    modifier = Modifier.size(13.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
