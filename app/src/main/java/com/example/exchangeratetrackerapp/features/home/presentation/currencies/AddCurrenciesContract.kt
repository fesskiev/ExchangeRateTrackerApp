package com.example.exchangeratetrackerapp.features.home.presentation.currencies

import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.CurrencySectionUi
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.CurrencyUi

data class AddCurrenciesState(
    val sections: List<CurrencySectionUi> = emptyList(),
    val filteredSections: List<CurrencySectionUi>? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface AddCurrenciesEvent {
    data object Done : AddCurrenciesEvent
    data object NavigateBack : AddCurrenciesEvent
    object Retry : AddCurrenciesEvent
    data class SearchQuery(val query: String) : AddCurrenciesEvent
    data class ToggleCurrencyItem(val currency: CurrencyUi) : AddCurrenciesEvent

}