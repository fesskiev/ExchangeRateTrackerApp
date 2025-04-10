package com.example.exchangeratetrackerapp.features.home.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exchangeratetrackerapp.features.home.presentation.assets.AddAssetsScreen
import com.example.exchangeratetrackerapp.features.home.presentation.rates.ExchangeRatesScreen

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.RATES.route) {
        composable(Screen.RATES.route) {
            ExchangeRatesScreen(
                onAddAssetClick = { navController.navigate(Screen.ADD_ASSETS.route) }
            )
        }
        composable(Screen.ADD_ASSETS.route) {
            AddAssetsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

private enum class Screen(
    val route: String
) {
    RATES("/rates"),
    ADD_ASSETS("/add_assets")
}