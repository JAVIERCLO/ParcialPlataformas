package com.jcl.parcialplataformas.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jcl.parcialplataformas.data.repository.DataSourceInfo
import com.jcl.parcialplataformas.model.Asset
import com.jcl.parcialplataformas.navigation.AssetDetail
import com.jcl.parcialplataformas.presentation.viewmodels.AssetsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsListScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: AssetsListViewModel = viewModel(
        factory = AssetsListViewModel.provideFactory(context)
    )

    val state by viewModel.uiState.collectAsState()

    val dataSourceLabel = when (val info = state.dataSourceInfo) {
        is DataSourceInfo.Remote -> "Viendo data más reciente"
        is DataSourceInfo.Local -> "Viendo data del ${info.lastSync}"
        DataSourceInfo.None -> ""
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Criptomonedas") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onSaveOfflineClicked() }
                ) {
                    Text("Ver offline (guardar)")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.loadAssets() }
                ) {
                    Text("Actualizar")
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    else -> {
                        AssetsList(
                            assets = state.assets,
                            onAssetClick = { asset ->
                                // 👇 AQUÍ YA NAVEGAS CON OBJETO SERIALIZABLE
                                navController.navigate(
                                    AssetDetail(assetId = asset.id)
                                )
                            }
                        )
                    }
                }
            }

            when {
                state.error != null -> {
                    Text(
                        text = state.error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                dataSourceLabel.isNotEmpty() -> {
                    Text(
                        text = dataSourceLabel,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetsList(
    assets: List<Asset>,
    onAssetClick: (Asset) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(assets) { asset ->
            AssetItemCard(
                asset = asset,
                onClick = { onAssetClick(asset) }
            )
        }
    }
}

@Composable
private fun AssetItemCard(
    asset: Asset,
    onClick: () -> Unit
) {
    val change = asset.changePercent24Hr.toDoubleOrNull()
    val isPositive = change != null && change >= 0.0
    val changeColor = if (isPositive) Color.Green else Color.Red

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = asset.symbol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Precio: \$${asset.priceUsd}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Cambio 24h: ${asset.changePercent24Hr}%",
                style = MaterialTheme.typography.bodyMedium,
                color = changeColor
            )
        }
    }
}