package com.jcl.parcialplataformas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id LIMIT 1")
    fun getAssetById(id: String): Flow<AssetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<AssetEntity>)
}