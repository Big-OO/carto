package com.shopify.carto.feature.orderhistory.presentation.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryTabUi

@Composable
fun OrderHistoryTabs(
    selectedTab: OrderHistoryTabUi,
    onTabClick: (OrderHistoryTabUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrderHistoryTabUi.entries.forEach { tab ->
            val selected = selectedTab == tab
            val containerColor by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                label = "orderTabColor",
            )
            val elevationPadding by animateDpAsState(
                targetValue = if (selected) 0.dp else 2.dp,
                label = "orderTabPadding",
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = elevationPadding)
                    .clip(RoundedCornerShape(7.dp))
                    .background(containerColor)
                    .clickable { onTabClick(tab) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when (tab) {
                        OrderHistoryTabUi.Ongoing -> stringResource(R.string.order_history_ongoing_tab)
                        OrderHistoryTabUi.Completed -> stringResource(R.string.order_history_completed_tab)
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                    color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
