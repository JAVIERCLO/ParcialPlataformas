package com.jcl.parcialplataformas.data.repository

import com.jcl.parcialplataformas.data.local.AssetDao
import com.jcl.parcialplataformas.data.local.AssetEntity
import com.jcl.parcialplataformas.data.local.OfflineConfig
import com.jcl.parcialplataformas.data.network.api.CoinCapApi
import com.jcl.parcialplataformas.model.Asset
import com.jcl.parcialplataformas.model.AssetDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AssetsRepoImplement(
    private val api: CoinCapApi,
    private val dao: AssetDao,
    private val offlinePrefs: OfflineConfig
) : AssetsRepository {

    override suspend fun fetchAssetsOnline(): Result<List<Asset>> {
        return api.getAssets().map { dtoList ->
            dtoList.map { dto ->
                Asset(
                    id = dto.id,
                    name = dto.name.orEmpty(),
                    symbol = dto.symbol.orEmpty(),
                    priceUsd = dto.priceUsd.orEmpty(),
                    changePercent24Hr = dto.changePercent24Hr.orEmpty()
                )
            }
        }
    }

    override suspend fun saveAssetsOffline(assets: List<Asset>, lastSync: String) {
        val entities = assets.map { asset ->
            AssetEntity(
                id = asset.id,
                name = asset.name,
                symbol = asset.symbol,
                priceUsd = asset.priceUsd,
                changePercent24Hr = asset.changePercent24Hr,
                supply = null,
                maxSupply = null,
                marketCapUsd = null
            )
        }
        dao.insertAll(entities)
        offlinePrefs.saveLastSyncDateTime(lastSync)
    }

    override fun getOfflineAssets(): Flow<AssetsResult?> {
        val flowAssets = dao.getAllAssets()
        val flowLastSync = offlinePrefs.getLastSyncDateTime()

        return flowAssets.combine(flowLastSync) { list, lastSync ->
            if (list.isEmpty()) {
                null
            } else {
                AssetsResult(
                    data = list.map { entity ->
                        Asset(
                            id = entity.id,
                            name = entity.name,
                            symbol = entity.symbol,
                            priceUsd = entity.priceUsd,
                            changePercent24Hr = entity.changePercent24Hr
                        )
                    },
                    source = if (lastSync != null) {
                        DataSourceInfo.Local(lastSync)
                    } else {
                        DataSourceInfo.None
                    }
                )
            }
        }
    }

    override suspend fun fetchAssetDetailOnline(id: String): Result<AssetDetail> {
        return api.getAssetById(id).map { dto ->
            AssetDetail(
                id = dto.id,
                name = dto.name.orEmpty(),
                symbol = dto.symbol.orEmpty(),
                priceUsd = dto.priceUsd.orEmpty(),
                changePercent24Hr = dto.changePercent24Hr.orEmpty(),
                supply = dto.supply,
                maxSupply = dto.maxSupply,
                marketCapUsd = dto.marketCapUsd
            )
        }
    }

    override fun getOfflineAssetDetail(id: String): Flow<AssetDetailResult?> {
        val flowEntity = dao.getAssetById(id)
        val flowLastSync = offlinePrefs.getLastSyncDateTime()

        return flowEntity.combine(flowLastSync) { entity, lastSync ->
            if (entity == null) {
                null
            } else {
                AssetDetailResult(
                    data = AssetDetail(
                        id = entity.id,
                        name = entity.name,
                        symbol = entity.symbol,
                        priceUsd = entity.priceUsd,
                        changePercent24Hr = entity.changePercent24Hr,
                        supply = entity.supply,
                        maxSupply = entity.maxSupply,
                        marketCapUsd = entity.marketCapUsd
                    ),
                    source = if (lastSync != null) {
                        DataSourceInfo.Local(lastSync)
                    } else {
                        DataSourceInfo.None
                    }
                )
            }
        }
    }
}