package com.jcl.parcialplataformas.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AssetsResponseDto(
    val data: List<AssetDto>
)

@Serializable
data class AssetResponseDto(
    val data: AssetDto
)

@Serializable
data class AssetDto(
    val id: String,
    val rank: String? = null,
    val symbol: String? = null,
    val name: String? = null,
    val supply: String? = null,
    val maxSupply: String? = null,
    val marketCapUsd: String? = null,
    val volumeUsd24Hr: String? = null,
    val priceUsd: String? = null,
    val changePercent24Hr: String? = null
)