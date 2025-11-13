package com.jcl.parcialplataformas.data.repository

import com.jcl.parcialplataformas.model.Asset
import com.jcl.parcialplataformas.model.AssetDetail
import kotlinx.coroutines.flow.Flow

sealed class DataSourceInfo {
    object Remote : DataSourceInfo()
    data class Local(val lastSync: String) : DataSourceInfo()
    object None : DataSourceInfo()
}

data class AssetsResult(
    val data: List<Asset>,
    val source: DataSourceInfo
)

data class AssetDetailResult(
    val data: AssetDetail,
    val source: DataSourceInfo
)

interface AssetsRepository {
    suspend fun fetchAssetsOnline(): Result<List<Asset>>
    suspend fun saveAssetsOffline(assets: List<Asset>, lastSync: String)
    fun getOfflineAssets(): Flow<AssetsResult?>
    suspend fun fetchAssetDetailOnline(id: String): Result<AssetDetail>
    fun getOfflineAssetDetail(id: String): Flow<AssetDetailResult?>
}
