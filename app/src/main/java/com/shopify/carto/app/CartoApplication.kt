package com.shopify.carto.app

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.shopify.carto.BuildConfig
import com.mapbox.common.MapboxOptions
import com.shopify.carto.feature.ai_integration.appfunctions.CartFunctions
import com.shopify.carto.feature.ai_integration.appfunctions.CompareFunctions
import com.shopify.carto.feature.ai_integration.appfunctions.OutfitFunctions
import com.shopify.carto.feature.ai_integration.appfunctions.SearchFunctions
import com.shopify.carto.feature.ai_integration.appfunctions.WishlistFunctions
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CartoApplication: Application(), AppFunctionConfiguration.Provider {

    @Inject
    lateinit var searchFunctions: SearchFunctions

    @Inject
    lateinit var cartFunctions: CartFunctions

    @Inject
    lateinit var wishlistFunctions: WishlistFunctions

    @Inject
    lateinit var compareFunctions: CompareFunctions

    @Inject
    lateinit var outfitFunctions: OutfitFunctions

    override fun onCreate() {
        super.onCreate()
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    }

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() = AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(SearchFunctions::class.java) { searchFunctions }
            .addEnclosingClassFactory(CartFunctions::class.java) { cartFunctions }
            .addEnclosingClassFactory(WishlistFunctions::class.java) { wishlistFunctions }
            .addEnclosingClassFactory(CompareFunctions::class.java) { compareFunctions }
            .addEnclosingClassFactory(OutfitFunctions::class.java) { outfitFunctions }
            .build()
}