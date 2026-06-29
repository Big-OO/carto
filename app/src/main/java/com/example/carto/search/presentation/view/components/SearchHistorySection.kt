package com.example.carto.search.presentation.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carto.R
import com.example.carto.search.domain.model.SearchHistoryItem

@Composable
fun SearchHistorySection(
    history: List<SearchHistoryItem>,
    onHistoryItemClicked: (String) -> Unit,
    onHistoryItemDeleted: (Long) -> Unit,
    onClearHistoryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recent Searches",
                modifier = Modifier
                    .weight(1f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
            )

            if (history.isNotEmpty()) {
                Text(
                    text = "Clear all",
                    modifier = Modifier.clickable(onClick = onClearHistoryClicked),
                    fontSize = 16.sp,
                    color = Color(0xFF111111),
                    textDecoration = TextDecoration.Underline,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.padding(top = 18.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            itemsIndexed(
                items = history,
                key = { _, item -> item.id }
            ) { index, item ->
                SearchHistoryRow(
                    item = item,
                    onClick = { onHistoryItemClicked(item.query) },
                    onDeleteClick = { onHistoryItemDeleted(item.id) },
                    showDivider = index != history.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun SearchHistoryRow(
    item: SearchHistoryItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.query,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = Color(0xFF222222),
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E),
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color(0xFFE7E7E7)
            )
        }
    }
}
