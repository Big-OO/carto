package com.example.carto.search.presentation.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carto.R

@Composable
fun SearchInputBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFE4E4E4),
                shape = RoundedCornerShape(16.dp),
            ),
        singleLine = true,
        textStyle = TextStyle(
            color = Color(0xFF111111),
            fontSize = 18.sp,
        ),
        cursorBrush = SolidColor(Color(0xFF111111)),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchSubmitted() }),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = Color(0xFF111111).copy(alpha = .3f)
                )

                Spacer(Modifier.width(18.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (query.isBlank()) {
                        Text(
                            text = "Search for clothes...",
                            color = Color(0xFFA9A9A9),
                            fontSize = 18.sp,
                        )
                    }
                    innerTextField()
                }

                Spacer(Modifier.width(16.dp))

                if (query.trim().isNotEmpty()) {
                    Icon(
                        modifier = Modifier.clickable(true, onClick = {
                            onQueryChanged("")
                        }),
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null,
                        tint = Color(0xFF111111).copy(alpha = if (query.isBlank()) 0.3f else 1f),
                    )
                }
            }
        },
    )
}
