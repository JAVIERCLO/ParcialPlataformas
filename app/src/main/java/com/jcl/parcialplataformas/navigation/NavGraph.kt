package com.jcl.parcialplataformas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jcl.parcialplataformas.presentation.screens.AssetDetailScreen
import com.jcl.parcialplataformas.presentation.screens.AssetsListScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.ASSETS_LIST
    ) {
        composable(NavRoutes.ASSETS_LIST) {
            AssetsListScreen(navController)
        }
        composable<AssetDetail> { backStackEntry ->
            val args: AssetDetail = backStackEntry.toRoute()
            AssetDetailScreen(
                navController = navController,
                assetId = args.assetId
            )
        }
    }
}