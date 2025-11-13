package com.jcl.parcialplataformas.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val Offline_Config = "offline_config"

val Context.dataStore by preferencesDataStore(Offline_Config)

class OfflineConfig(
    private val context: Context
) {

    companion object {
        private val LAST_SYNC_KEY = stringPreferencesKey("last_sync_datetime")
    }

    fun getLastSyncDateTime(): Flow<String?> {
        return context.dataStore.data.map { prefs: Preferences ->
            prefs[LAST_SYNC_KEY]
        }
    }

    suspend fun saveLastSyncDateTime(value: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SYNC_KEY] = value
        }
    }
}