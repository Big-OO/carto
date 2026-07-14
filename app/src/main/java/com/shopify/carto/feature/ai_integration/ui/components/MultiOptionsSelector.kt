package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class MultiSelectorConfig(
    val name: String,
    val displayName: String,
    val options: List<String>
)

fun parseMultiSelectorConfig(option: String): MultiSelectorConfig? {
    val trimmed = option.trim()
    val openParen = trimmed.indexOf('(')
    val closeParen = trimmed.lastIndexOf(')')
    if (openParen <= 0 || closeParen != trimmed.length - 1 || openParen >= closeParen) {
        return null
    }
    val name = trimmed.substring(0, openParen).trim()
    val valuesStr = trimmed.substring(openParen + 1, closeParen)
    val values = valuesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    if (name.isEmpty() || values.isEmpty()) return null

    val displayName = when (name.lowercase()) {
        "quantity" -> "Quantity"
        "size" -> "Size"
        "color" -> "Color"
        else -> name.replaceFirstChar { it.uppercase() }
    }
    return MultiSelectorConfig(name = name.lowercase(), displayName = displayName, options = values)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiOptionsSelector(
    configs: List<MultiSelectorConfig>,
    onSelectionComplete: (String) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val selectedOptions = remember(configs) { mutableStateMapOf<String, String>() }

    // Pre-select quantity = "1" if available
    LaunchedEffect(configs) {
        val qtyConfig = configs.firstOrNull { it.name == "quantity" }
        if (qtyConfig != null && qtyConfig.options.contains("1") && !selectedOptions.containsKey("quantity")) {
            selectedOptions["quantity"] = "1"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            configs.forEachIndexed { index, config ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = config.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        config.options.forEach { option ->
                            val isSelected = selectedOptions[config.name] == option
                            val activeColor = MaterialTheme.colorScheme.primary
                            val inactiveColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            
                            val containerColor by animateColorAsState(
                                targetValue = if (isSelected) activeColor else inactiveColor,
                                label = "containerColor"
                            )
                            val contentColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                              else MaterialTheme.colorScheme.onPrimaryContainer,
                                label = "contentColor"
                            )

                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale by animateFloatAsState(
                                targetValue = if (isPressed) 0.95f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "chipScale"
                            )

                            Surface(
                                onClick = {
                                    if (isEnabled) {
                                        selectedOptions[config.name] = option
                                        // Check if all configs are selected
                                        val allSelected = configs.all { selectedOptions.containsKey(it.name) }
                                        if (allSelected) {
                                            // Format string: Quantity: X, Size: Y, Color: Z
                                            val parts = configs.map { c ->
                                                "${c.displayName}: ${selectedOptions[c.name]}"
                                            }
                                            onSelectionComplete(parts.joinToString(", "))
                                        }
                                    }
                                },
                                enabled = isEnabled,
                                shape = RoundedCornerShape(20.dp),
                                color = containerColor,
                                contentColor = contentColor,
                                border = if (isSelected) null else BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ),
                                interactionSource = interactionSource,
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
                
                if (index < configs.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
