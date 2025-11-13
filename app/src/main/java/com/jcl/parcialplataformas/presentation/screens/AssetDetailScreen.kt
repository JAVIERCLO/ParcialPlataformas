package com.jcl.parcialplataformas.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jcl.parcialplataformas.data.repository.DataSourceInfo
import com.jcl.parcialplataformas.presentation.viewmodels.AssetDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(
    navController: NavHostController,
    assetId: String
) {
    val context = LocalContext.current
    val viewModel: AssetDetailViewModel = viewModel(
        factory = AssetDetailViewModel.provideFactory(context)
    )

    LaunchedEffect(assetId) {
        viewModel.loadAsset(assetId)
    }

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Asset") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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

                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                state.asset != null -> {
                    val asset = state.asset
                    val dataSourceLabel = when (val info = state.dataSourceInfo) {
                        is DataSourceInfo.Remote -> "Viendo data más reciente"
                        is DataSourceInfo.Local -> "Viendo data del ${info.lastSync}"
                        DataSourceInfo.None -> ""
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (dataSourceLabel.isNotEmpty()) {
                            Text(
                                text = dataSourceLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "${asset?.name} (${asset?.symbol})",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Precio: \$${asset?.priceUsd}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "Cambio 24h: ${asset?.changePercent24Hr}%",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Supply: ${asset?.supply ?: "-"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Max Supply: ${asset?.maxSupply ?: "-"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Market Cap USD: ${asset?.marketCapUsd ?: "-"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
