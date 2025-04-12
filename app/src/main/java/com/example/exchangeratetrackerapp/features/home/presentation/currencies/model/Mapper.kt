package com.example.exchangeratetrackerapp.features.home.presentation.currencies.model

import com.example.exchangeratetrackerapp.core.common.cryptoCurrencyCodes
import com.example.exchangeratetrackerapp.core.common.popularCurrencyCodes
import com.example.exchangeratetrackerapp.core.common.getCurrencySymbol
import com.example.exchangeratetrackerapp.core.data.remote.CurrencyResponse

fun CurrencyResponse.mapResponseToUiModel(
    selectedCurrencies: List<String>
): List<CurrencySectionUi> {
    val popularCurrencies = mutableListOf<CurrencyUi>()
    val cryptoCurrencies = mutableListOf<CurrencyUi>()
    val otherCurrencies = mutableListOf<CurrencyUi>()

    currencies.forEach { (code, name) ->
        val isSelected = code in selectedCurrencies
        val currencyUi = CurrencyUi(
            code = code,
            name = name,
            symbol = code.getCurrencySymbol(),
            isSelected = isSelected
        )

        when (code) {
            in cryptoCurrencyCodes -> cryptoCurrencies.add(currencyUi)
            in popularCurrencyCodes -> popularCurrencies.add(currencyUi)
            else -> otherCurrencies.add(currencyUi)
        }
    }

    return listOfNotNull(
        if (popularCurrencies.isNotEmpty()) CurrencySectionUi(
            CurrencySectionType.POPULAR_ASSETS,
            popularCurrencies
        ) else null,
        if (cryptoCurrencies.isNotEmpty()) CurrencySectionUi(
            CurrencySectionType.CRYPTOCURRENCIES,
            cryptoCurrencies
        ) else null,
        if (otherCurrencies.isNotEmpty()) CurrencySectionUi(
            CurrencySectionType.OTHER_CURRENCIES,
            otherCurrencies
        ) else null
    )
}