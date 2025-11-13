package com.jcl.parcialplataformas.data.network.api

import com.jcl.parcialplataformas.data.network.dto.AssetDto
import com.jcl.parcialplataformas.data.network.dto.AssetResponseDto
import com.jcl.parcialplataformas.data.network.dto.AssetsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CoinCapApi(
    private val httpClient: HttpClient
) {

    suspend fun getAssets(): Result<List<AssetDto>> {
        return try {
            val response: AssetsResponseDto = httpClient.get("assets").body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAssetById(id: String): Result<AssetDto> {
        return try {
            val response: AssetResponseDto = httpClient.get("assets/$id").body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
