package com.example.exchangeratetrackerapp.features.home.presentation.rates.model

import com.example.exchangeratetrackerapp.core.data.local.StoredRatesData

fun mapSelectedCurrenciesToUiModel(
    latestRates: StoredRatesData?,
    previousRatesData: StoredRatesData?,
    selectedCurrencies: List<String>
) = selectedCurrencies.map { currencyCode ->
    val currentRate = latestRates?.rates[currencyCode]
    val percentChange = if (previousRatesData != null && currentRate != null) {
        val previousRate = previousRatesData.rates[currencyCode] ?: currentRate
        calculatePercentChange(currentRate, previousRate)
    } else {
        0.0
    }
    ExchangeRateUi(
        code = currencyCode,
        rate = currentRate ?: 0.0,
        percentChange = percentChange
    )
}

private fun calculatePercentChange(current: Double, previous: Double): Double {
    if (previous == 0.0) return 0.0
    return ((current - previous) / previous) * 100.0
}