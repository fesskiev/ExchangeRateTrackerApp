package com.example.exchangeratetrackerapp.features.home.presentation.currencies.model

enum class CurrencySectionType {
    POPULAR_ASSETS,
    CRYPTOCURRENCIES,
    OTHER_CURRENCIES
}

data class CurrencySectionUi(
    val type: CurrencySectionType,
    val currencies: List<CurrencyUi>
)