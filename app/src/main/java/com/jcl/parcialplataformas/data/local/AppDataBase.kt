package com.jcl.parcialplataformas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AssetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
}