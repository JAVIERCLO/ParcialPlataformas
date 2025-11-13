package com.jcl.parcialplataformas.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.jcl.parcialplataformas.data.local.AppDatabase
import com.jcl.parcialplataformas.data.local.OfflineConfig
import com.jcl.parcialplataformas.data.network.api.CoinCapApi
import com.jcl.parcialplataformas.data.network.HttpClientFactory
import com.jcl.parcialplataformas.data.repository.AssetsRepository
import com.jcl.parcialplataformas.data.repository.AssetsRepoImplement
import com.jcl.parcialplataformas.data.repository.DataSourceInfo
import com.jcl.parcialplataformas.model.Asset
import com.jcl.parcialplataformas.presentation.AssetsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AssetsListViewModel(
    private val repository: AssetsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetsState())
    val uiState: StateFlow<AssetsState> = _uiState

    init {
        loadAssets()
    }

    fun loadAssets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            val remoteResult = repository.fetchAssetsOnline()

            remoteResult
                .onSuccess { list ->
                    _uiState.value = AssetsState(
                        isLoading = false,
                        error = null,
                        assets = list,
                        dataSourceInfo = DataSourceInfo.Remote
                    )
                }
                .onFailure {
                    val offline = repository.getOfflineAssets().firstOrNull()
                    if (offline != null) {
                        _uiState.value = AssetsState(
                            isLoading = false,
                            error = null,
                            assets = offline.data,
                            dataSourceInfo = offline.source
                        )
                    } else {
                        _uiState.value = AssetsState(
                            isLoading = false,
                            error = "No se pudo cargar información (sin internet y sin datos guardados)",
                            assets = emptyList(),
                            dataSourceInfo = DataSourceInfo.None
                        )
                    }
                }
        }
    }

    fun onSaveOfflineClicked() {
        viewModelScope.launch {
            val currentAssets: List<Asset> = _uiState.value.assets
            if (currentAssets.isEmpty()) return@launch

            val nowString = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

            repository.saveAssetsOffline(currentAssets, nowString)
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val appContext = context.applicationContext

                    val db = Room.databaseBuilder(
                        appContext,
                        AppDatabase::class.java,
                        "coincap-db"
                    ).build()

                    val dao = db.assetDao()
                    val prefs = OfflineConfig(appContext)
                    val api = CoinCapApi(HttpClientFactory.create())
                    val repo: AssetsRepository =
                        AssetsRepoImplement(api, dao, prefs)

                    return AssetsListViewModel(repo) as T
                }
            }
    }
}
