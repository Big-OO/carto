package com.shopify.carto.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.ui.theme.CartoTheme

@Composable
fun TextField(
    title: String,
    value: String,
    placeholder: String? = null,
    isValidate: Boolean = false,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    val hasError = errorMessage != null
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val outlineColor = MaterialTheme.colorScheme.outline
    val successColor = CartoTheme.colors.tertiary

    val borderColor = when {
        hasError    -> errorColor
        isValidate  -> successColor
        value.isNotEmpty() -> primaryColor
        else        -> outlineColor
    }
    
    val labelColor = if (value.isNotEmpty() || hasError) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Spring-bounced scale for the check icon
    val checkScale by animateFloatAsState(
        targetValue = if (isValidate) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkScale"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            isError = hasError,
            visualTransformation = if (isPassword && !isPasswordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            placeholder = {
                if (placeholder != null){
                    Text(
                        placeholder, style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            },
            trailingIcon = {
                when {
                    isPassword -> {
                        IconButton(onClick = { onPasswordToggle?.invoke() }) {
                            Icon(
                                imageVector = if (isPasswordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = "Password visibility",
                                tint = if (isPasswordVisible) successColor else errorColor
                            )
                        }
                    }
                    isValidate -> {
                        AnimatedVisibility(
                            visible = isValidate,
                            enter = fadeIn(tween(200)) + scaleIn(
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Valid",
                                tint = successColor,
                                modifier = Modifier.scale(checkScale)
                            )
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                errorBorderColor = errorColor,
                focusedLabelColor = labelColor,
                unfocusedLabelColor = labelColor,
                cursorColor = primaryColor,
                errorTrailingIconColor = errorColor,
            ),
            supportingText = if (hasError) {
                {
                    AnimatedVisibility(
                        visible = hasError,
                        enter = fadeIn(tween(200))
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = errorColor,
                            fontSize = 11.sp
                        )
                    }
                }
            } else null
        )
    }
}