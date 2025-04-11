package com.example.exchangeratetrackerapp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

interface CurrenciesCache {
    suspend fun getSelectedCurrencies(): List<String>
    suspend fun saveSelectedCurrencies(currencies: List<String>)
    suspend fun removeCurrency(currency: String)
}

class CurrenciesCacheImpl(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : CurrenciesCache {

    companion object {
        private const val SELECTED_CURRENCIES_KEY = "selected_currencies"
        private val DEFAULT_CURRENCIES = listOf("USD", "EUR", "BTC")
    }

    private val selectedCurrenciesKey = stringPreferencesKey(SELECTED_CURRENCIES_KEY)

    override suspend fun getSelectedCurrencies() =
        dataStore.getOnes(selectedCurrenciesKey)?.let { jsonString ->
            json.decodeFromString<List<String>>(jsonString)
        } ?: DEFAULT_CURRENCIES

    override suspend fun saveSelectedCurrencies(currencies: List<String>) {
        dataStore.put(
            selectedCurrenciesKey,
            json.encodeToString(ListSerializer(String.serializer()), currencies)
        )
    }

    override suspend fun removeCurrency(currency: String) {
        val currentCurrencies = getSelectedCurrencies().toMutableList()
        if (currentCurrencies.contains(currency)) {
            currentCurrencies.remove(currency)
            saveSelectedCurrencies(currentCurrencies)
        }
    }
}