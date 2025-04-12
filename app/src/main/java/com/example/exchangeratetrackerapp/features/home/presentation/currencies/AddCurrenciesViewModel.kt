package com.example.exchangeratetrackerapp.features.home.presentation.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangeratetrackerapp.core.data.local.CurrenciesCache
import com.example.exchangeratetrackerapp.core.data.remote.ExchangeRateApi
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.CurrencySectionUi
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.CurrencyUi
import com.example.exchangeratetrackerapp.features.home.presentation.currencies.model.mapResponseToUiModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCurrenciesViewModel(
    private val api: ExchangeRateApi,
    private val currenciesCache: CurrenciesCache
) : ViewModel() {

    var state = MutableStateFlow(AddCurrenciesState())
        private set
    var events = MutableSharedFlow<AddCurrenciesEvent>()
        private set

    init {
        fetchCurrencies()
    }

    fun onEvent(event: AddCurrenciesEvent) = when (event) {
        is AddCurrenciesEvent.Done -> saveSelectedCurrencies()
        is AddCurrenciesEvent.SearchQuery -> updateSearchQuery(event.query)
        is AddCurrenciesEvent.ToggleCurrencyItem -> toggleCurrencyItem(event.currency)
        is AddCurrenciesEvent.Retry -> fetchCurrencies()
        else -> {}
    }

    private fun fetchCurrencies() {
        viewModelScope.launch {
            runCatching {
                setLoadingState()
                val response = api.getCurrencies()
                val selectedCurrencies = currenciesCache.getSelectedCurrencies()
                val sections = response.mapResponseToUiModel(selectedCurrencies)
                setSectionsState(sections)
            }.onFailure { setErrorState(it) }
        }
    }

    private fun saveSelectedCurrencies() {
        viewModelScope.launch {
            runCatching {
                val selectedCurrencies = state.value.sections
                    .flatMap { it.currencies }
                    .filter { it.isSelected }
                    .map { it.code }
                currenciesCache.saveSelectedCurrencies(selectedCurrencies)
                events.emit(AddCurrenciesEvent.NavigateBack)
            }.onFailure { setErrorState(it) }
        }
    }

    private fun updateSearchQuery(query: String) {
        state.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredSections = if (query.isBlank()) {
                    null
                } else {
                    currentState.sections.map { section ->
                        section.copy(
                            currencies = section.currencies.filter {
                                it.name.contains(query, ignoreCase = true)
                            }
                        )
                    }.filter { it.currencies.isNotEmpty() }
                }
            )
        }
    }

    private fun toggleCurrencyItem(currency: CurrencyUi) {
        state.update {
            it.copy(
                sections = it.sections.map { section ->
                    section.copy(
                        currencies = section.currencies.map {
                            if (it.code == currency.code) {
                                it.copy(isSelected = !it.isSelected)
                            } else {
                                it
                            }
                        }
                    )
                }
            )
        }
    }

    private fun setErrorState(error: Throwable) {
        state.update {
            it.copy(
                isLoading = false,
                sections = listOf(),
                searchQuery = "",
                error = error.message
            )
        }
    }

    private fun setLoadingState() {
        state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
    }

    private fun setSectionsState(sections: List<CurrencySectionUi>) {
        state.update {
            it.copy(
                isLoading = false,
                sections = sections,
                searchQuery = "",
                error = null
            )
        }
    }

}

