package com.jcl.parcialplataformas.presentation

import com.jcl.parcialplataformas.data.repository.DataSourceInfo
import com.jcl.parcialplataformas.model.AssetDetail

data class AssetDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val asset: AssetDetail? = null,
    val dataSourceInfo: DataSourceInfo = DataSourceInfo.None
)