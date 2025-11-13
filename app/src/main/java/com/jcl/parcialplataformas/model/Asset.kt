package com.jcl.parcialplataformas.model

import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: String,
    val changePercent24Hr: String
)

@Serializable
data class AssetDetail(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val supply: String?,
    val maxSupply: String?,
    val marketCapUsd: String?
)