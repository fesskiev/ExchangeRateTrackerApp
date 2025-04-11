package com.example.exchangeratetrackerapp.features.home.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.AddACurrenciesScreen
import com.example.exchangeratetrackerapp.features.home.presentation.rates.ExchangeRatesScreen

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.RATES.route) {
        composable(Screen.RATES.route) {
            ExchangeRatesScreen(
                onAddCurrenciesClick = { navController.navigate(Screen.ADD_CURRENCIES.route) }
            )
        }
        composable(Screen.ADD_CURRENCIES.route) {
            AddACurrenciesScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

private enum class Screen(
    val route: String
) {
    RATES("/rates"),
    ADD_CURRENCIES("/add_currencies")
}