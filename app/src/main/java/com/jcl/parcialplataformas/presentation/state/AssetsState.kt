package com.jcl.parcialplataformas.presentation

import com.jcl.parcialplataformas.model.Asset
import com.jcl.parcialplataformas.data.repository.DataSourceInfo

data class AssetsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val assets: List<Asset> = emptyList(),
    val dataSourceInfo: DataSourceInfo = DataSourceInfo.None
)