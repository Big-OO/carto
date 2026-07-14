package com.shopify.carto.feature.map.domain.model

data class SelectedMapAddress(
    val point: MapPoint,
    val address: MapAddress,
)
