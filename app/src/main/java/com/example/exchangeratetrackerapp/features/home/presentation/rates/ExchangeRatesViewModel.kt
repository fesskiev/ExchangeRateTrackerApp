package com.example.exchangeratetrackerapp.features.home.presentation.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangeratetrackerapp.core.data.local.CurrenciesCache
import com.example.exchangeratetrackerapp.core.data.local.ExchangeRatesCache
import com.example.exchangeratetrackerapp.core.data.local.mapResponseToStoredRatesData
import com.example.exchangeratetrackerapp.core.data.remote.ExchangeRateApi
import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.ExchangeRateUi
import com.example.exchangeratetrackerapp.features.home.presentation.rates.model.mapSelectedCurrenciesToUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ExchangeRatesViewModel(
    private val api: ExchangeRateApi,
    private val exchangeRatesCache: ExchangeRatesCache,
    private val currenciesCache: CurrenciesCache
) : ViewModel() {

    companion object {
        private const val UPDATE_INTERVAL_MS = 5000L
    }

    var state = MutableStateFlow(ExchangeRatesState())
        private set
    private var job: Job? = null

    fun onEvent(event: ExchangeRatesEvent) = when (event) {
        is ExchangeRatesEvent.RemoveCurrency -> removeCurrency(event.code)
        is ExchangeRatesEvent.StartPeriodicUpdates -> startPeriodicUpdates()
        is ExchangeRatesEvent.UpdateCurrencies -> updateCurrencies()
        is ExchangeRatesEvent.StopPeriodicUpdates -> stopPeriodicUpdates()
        else -> {}
    }

    private fun removeCurrency(code: String) {
        viewModelScope.launch {
            runCatching {
                currenciesCache.removeCurrency(code)
                val currencies = currenciesCache.getSelectedCurrencies()
                if (currencies.isEmpty()) {
                    stopPeriodicUpdates()
                    setEmptyCurrenciesState()
                } else {
                    updateSelectedCurrencies()
                }
            }.onFailure { error -> setErrorState(error) }
        }
    }

    private fun updateCurrencies() {
        viewModelScope.launch {
            runCatching {
                updateSelectedCurrencies()
            }.onFailure { error -> setErrorState(error) }
        }
    }

    private fun startPeriodicUpdates() {
        job?.cancel()
        job = viewModelScope.launch {
            val currencies = currenciesCache.getSelectedCurrencies()
            if (currencies.isNotEmpty()) {
                while (isActive) {
                    fetchLatestRates()
                    delay(UPDATE_INTERVAL_MS)
                }
            } else {
                setEmptyCurrenciesState()
            }
        }
    }

    private fun stopPeriodicUpdates() {
        job?.cancel()
        job = null
    }

    private suspend fun fetchLatestRates() {
        runCatching {
            setLoadingState()
            val response = api.getLatestExchangeRates()
            exchangeRatesCache.saveRates(response.mapResponseToStoredRatesData())
            updateSelectedCurrencies()
        }.onFailure { error -> setErrorState(error) }
    }

    private suspend fun updateSelectedCurrencies() {
        val latestRates = exchangeRatesCache.getLatestRates()
        val rates = mapSelectedCurrenciesToUiModel(
            latestRates = latestRates,
            previousRatesData = exchangeRatesCache.getPreviousRates(),
            selectedCurrencies = currenciesCache.getSelectedCurrencies()
        )
        setCurrenciesState(
            rates = rates,
            lastUpdate = latestRates?.updateTime
        )
    }

    private fun setCurrenciesState(rates: List<ExchangeRateUi>, lastUpdate: String?) {
        state.update {
            it.copy(
                isLoading = false,
                rates = rates,
                lastUpdate = lastUpdate,
                error = null
            )
        }
    }

    private fun setEmptyCurrenciesState() {
        state.update {
            it.copy(
                isLoading = false,
                rates = listOf(),
                lastUpdate = null
            )
        }
    }

    private fun setErrorState(error: Throwable) {
        state.update {
            it.copy(
                isLoading = false,
                rates = listOf(),
                lastUpdate = null,
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

}