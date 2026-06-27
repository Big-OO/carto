package com.example.carto.home.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.drawerlayout.R
import coil.compose.AsyncImage
import com.example.carto.home.domain.mappers.Product
import com.example.carto.home.presentation.HomeUiState
import com.example.carto.home.presentation.HomeViewModel


data class Vendor(val id: Int, val name: String)
data class NavItem(val label: String, val icon: ImageVector)

private val fakeVendors = listOf(
    Vendor(1, "TechZone"),
    Vendor(2, "GadgetHub"),
    Vendor(3, "ElectroMart"),
    Vendor(4, "ByteStore"),
    Vendor(5, "NextGen")
)

private val navItems = listOf(
    NavItem("Home", Icons.Filled.Home),
    NavItem("Search", Icons.Outlined.Search),
    NavItem("Saved", Icons.Outlined.FavoriteBorder),
    NavItem("Cart", Icons.Outlined.ShoppingCart),
    NavItem("Account", Icons.Outlined.AccountCircle)
)


@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                items = navItems,
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { /* TODO: notification click */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            }
            item { AdsCard() }

            item { SectionTitle("All Products") }
            item {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingBox()
                    is HomeUiState.Error -> ErrorBox(
                        message = state.message,
                        onRetry = viewModel::fetchProducts
                    )
                    is HomeUiState.Success -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height(420.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.products) { ProductCard(it) }
                        }
                    }
                }
            }

            item { SectionTitle("All Vendors") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(fakeVendors) { VendorItem(it) }
                }
            }
        }
    }
}


@Composable
fun AdsCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
          // Image(painter = painterResource(id = R.), contentDescription = "Ads Image")

            Text("Special Offer — Up to 30% Off", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(product.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            Text(
                "$${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VendorItem(vendor: Vendor) {
    Column(
        modifier = Modifier.width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )
        Spacer(Modifier.height(4.dp))
        Text(vendor.name, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@Composable
fun AppBottomBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Failed to load products: $message", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}