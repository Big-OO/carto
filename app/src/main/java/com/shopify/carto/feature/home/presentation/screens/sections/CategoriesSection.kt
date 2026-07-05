//package com.shopify.carto.feature.home.presentation.screens.sections
//
//
package com.shopify.carto.feature.home.presentation.screens.sections


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.feature.home.domain.model.Category
import com.shopify.carto.feature.home.presentation.screens.components.CategoryCard
import com.shopify.carto.feature.home.presentation.screens.components.SectionHeader
@Composable
fun CategoriesSection(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    onSeeAll: () -> Unit
) {

    Column {

        SectionHeader(
            title = stringResource(R.string.homeCategoriesTitle),
            onSeeAll = onSeeAll
        )

        Spacer(Modifier.height(4.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            items(categories) { category ->

                CategoryCard(
                    category = category,
                    onClick = onCategoryClick
                )

            }

        }

    }
}

//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.shopify.carto.feature.home.domain.model.Category
//import com.shopify.carto.feature.home.presentation.screens.components.CategoryCard
//import com.shopify.carto.feature.home.presentation.screens.components.SectionHeader
//@Composable
//fun CategoriesSection(
//    categories: List<Category>,
//    onCategoryClick: (Category) -> Unit,
//    onSeeAll: () -> Unit
//) {
//
//    Column {
//
//        SectionHeader(
//            title = "Categories",
//            onSeeAll = onSeeAll
//        )
//
//        Spacer(Modifier.height(4.dp))
//
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(0.dp)
//        ) {
//
//            items(categories) { category ->
//
//                CategoryCard(
//                    category = category,
//                    onClick = onCategoryClick
//                )
//
//            }
//
//        }
//
//    }
//}
