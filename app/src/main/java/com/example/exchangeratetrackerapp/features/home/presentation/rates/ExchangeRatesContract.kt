package com.example.exchangeratetrackerapp.features.home.presentation.rates

import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.ExchangeRateUi

data class ExchangeRatesState(
    val rates: List<ExchangeRateUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ExchangeRatesEvent {
    object AddAssets : ExchangeRatesEvent
    object RemoveAsset : ExchangeRatesEvent
    object Retry : ExchangeRatesEvent
}