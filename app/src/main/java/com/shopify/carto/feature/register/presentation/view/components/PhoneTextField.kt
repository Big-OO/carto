package com.shopify.carto.feature.register.presentation.view.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.R
import com.shopify.carto.ui.theme.CartoTheme


@Composable
fun PhoneTextField(
    title: String,
    value: String,
    placeholder: String? = null,
    isValidate: Boolean = false,
    errorMessage: String? = null,
    maxLength: Int = 10,
    onValueChange: (String) -> Unit,
) {
    val hasError = errorMessage != null
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val outlineColor = MaterialTheme.colorScheme.outline
    val successColor = CartoTheme.colors.tertiary

    val borderColor = when {
        hasError -> errorColor
        isValidate -> successColor
        value.isNotEmpty() -> primaryColor
        else -> outlineColor
    }

    val labelColor = if (value.isNotEmpty() || hasError) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val checkScale by animateFloatAsState(
        targetValue = if (isValidate) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "phoneCheckScale"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                val digitsOnly = input
                    .filter { it.isDigit() }
                    .take(maxLength)

                onValueChange(digitsOnly)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            isError = hasError,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            prefix = {
                Text(
                    text = stringResource(R.string.register_phone_number_country_code),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 6.dp)
                )
            },
            placeholder = {
                if (placeholder != null) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            },
            trailingIcon = {
                if (isValidate) {
                    AnimatedVisibility(
                        visible = true,
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
                        visible = true,
                        enter = fadeIn(tween(200))
                    ) {
                        Text(
                            text = errorMessage,
                            color = errorColor,
                            fontSize = 11.sp
                        )
                    }
                }
            } else null
        )
    }
}