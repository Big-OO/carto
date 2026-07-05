package com.shopify.carto.feature.map.data.datasource

import com.shopify.carto.feature.map.data.error.SearchError
import com.shopify.carto.feature.map.data.result.MapDataResult
import com.shopify.carto.feature.map.domain.model.MapPoint
import com.shopify.carto.feature.map.domain.model.MapSearchSuggestion
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSuggestionsCallback
import com.mapbox.search.common.RestrictedMapboxSearchAPI
import com.mapbox.search.result.SearchSuggestion
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class MapboxSearchDataSource @Inject constructor() {
    private val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
        settings = SearchEngineSettings(),
        apiType = ApiType.GEOCODING,
    )

    @OptIn(RestrictedMapboxSearchAPI::class)
    suspend fun search(query: String): MapDataResult<List<MapSearchSuggestion>, SearchError> {
        if (query.isBlank()) return MapDataResult.Success(emptyList())

        return suspendCancellableCoroutine { continuation ->
            searchEngine.search(
                query = query,
                options = SearchOptions(limit = 6),
                callback = object : SearchSuggestionsCallback {
                    override fun onSuggestions(
                        suggestions: List<SearchSuggestion>,
                        responseInfo: ResponseInfo,
                    ) {
                        val mapped = suggestions.map { suggestion ->
                            MapSearchSuggestion(
                                name = suggestion.name,
                                address = suggestion.address?.formattedAddress(),
                                point = suggestion.coordinate?.let { point ->
                                    MapPoint(
                                        latitude = point.latitude(),
                                        longitude = point.longitude(),
                                    )
                                },
                            )
                        }
                        continuation.resume(MapDataResult.Success(mapped))
                    }

                    override fun onError(e: Exception) {
                        continuation.resume(MapDataResult.Failure(e.message, SearchError.UnKnown))
                    }
                },
            )
        }
    }
}
