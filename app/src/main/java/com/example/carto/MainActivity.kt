package com.example.carto


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carto.home.data.network.RetrofitProvider
import com.example.carto.home.data.repository.HomeRepositoryImp
import com.example.carto.home.presentation.screens.HomeScreen
import com.example.carto.home.presentation.HomeViewModel
import com.example.carto.home.presentation.HomeViewModelFactory
import com.example.carto.ui.theme.CartoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CartoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val api = RetrofitProvider.create(
                        hostName = "mad46-and7.myshopify.com",
                        accessToken = "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"
                    )
                    val repository = HomeRepositoryImp(api)
                    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(
                        repository
                    )
                    )
                    HomeScreen(viewModel = homeViewModel)
                }
            }
        }
    }
}

