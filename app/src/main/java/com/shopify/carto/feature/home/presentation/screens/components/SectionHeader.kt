//package com.shopify.carto.feature.home.presentation.screens.components
//
package com.shopify.carto.feature.home.presentation.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.shopify.carto.R

@Composable
fun SectionHeader(
    title: String,
    onSeeAll: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        TextButton(onClick = onSeeAll) {

            Text(stringResource(R.string.commonSeeAll))

        }

    }

}
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//
//@Composable
//fun SectionHeader(
//    title: String,
//    onSeeAll: () -> Unit
//) {
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//
//        Text(
//            title,
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold
//        )
//
//        TextButton(onClick = onSeeAll) {
//
//            Text("See All")
//
//        }
//
//    }
//
//}