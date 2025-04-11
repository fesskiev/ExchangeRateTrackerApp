package com.example.exchangeratetrackerapp.features.home.presentation.rates

import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.ExchangeRateUi

data class ExchangeRatesState(
    val rates: List<ExchangeRateUi> = emptyList(),
    val lastUpdate: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ExchangeRatesEvent {
    data object AddCurrencies : ExchangeRatesEvent
    data class RemoveCurrency(val code: String) : ExchangeRatesEvent
    data object StartPeriodicUpdates : ExchangeRatesEvent
    data object UpdateCurrencies : ExchangeRatesEvent
    object StopPeriodicUpdates : ExchangeRatesEvent
}