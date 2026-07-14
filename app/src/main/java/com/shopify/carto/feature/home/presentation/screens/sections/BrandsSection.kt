//package com.shopify.carto.feature.home.presentation.screens.sections
//
package com.shopify.carto.feature.home.presentation.screens.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.feature.home.presentation.screens.components.SectionHeader
import com.shopify.carto.feature.home.presentation.screens.components.BrandCard
import androidx.compose.runtime.Composable
import com.shopify.carto.feature.home.domain.model.Brand

@Composable
fun BrandsSection(
    brands: List<Brand>,
    onSeeAll: () -> Unit,
    onBrandClick: (Brand) -> Unit
) {
    Column {
        SectionHeader(title = stringResource(R.string.homeBrandsTitle), onSeeAll = onSeeAll)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(brands) { brand ->
                BrandCard(brand = brand,
                    compact = true,
                    onBrandClick = onBrandClick)
            }
        }
    }
}

//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.shopify.carto.feature.home.presentation.screens.components.SectionHeader
//import com.shopify.carto.feature.home.presentation.screens.components.BrandCard
//import androidx.compose.runtime.Composable
//import com.shopify.carto.feature.home.domain.model.Brand
//
//@Composable
//fun BrandsSection(
//    brands: List<Brand>,
//    onSeeAll: () -> Unit,
//    onBrandClick: (Brand) -> Unit
//) {
//    Column {
//        SectionHeader(title = "All Brands", onSeeAll = onSeeAll)
//        Spacer(Modifier.height(8.dp))
//        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            items(brands) { brand ->
//                BrandCard(brand = brand,
//                    compact = true,
//                    onBrandClick = onBrandClick)
//            }
//        }
//    }
//}
