package com.example.exchangeratetrackerapp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.json.Json

interface ExchangeRatesCache {
    suspend fun getLatestRates(): StoredRatesData?
    suspend fun getPreviousRates(): StoredRatesData?
    suspend fun saveRates(storedRatesData: StoredRatesData)
}

class ExchangeRatesCacheImpl(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : ExchangeRatesCache {

    companion object {
        private const val LATEST_RATES_KEY = "latest_rates"
        private const val PREVIOUS_RATES_KEY = "previous_rates"
    }

    private val latestRatesKey = stringPreferencesKey(LATEST_RATES_KEY)
    private val previousRatesKey = stringPreferencesKey(PREVIOUS_RATES_KEY)

    override suspend fun getLatestRates() =
        dataStore.getOnes(latestRatesKey)?.let { jsonString ->
            json.decodeFromString<StoredRatesData>(jsonString)
        }

    override suspend fun getPreviousRates() =
        dataStore.getOnes(previousRatesKey)?.let { jsonString ->
            json.decodeFromString<StoredRatesData>(jsonString)
        }

    override suspend fun saveRates(storedRatesData: StoredRatesData) {
        val currentLatest = getLatestRates()
        if (currentLatest != null) {
            dataStore.put(
                previousRatesKey,
                json.encodeToString(StoredRatesData.serializer(), currentLatest)
            )
        }
        dataStore.put(
            latestRatesKey,
            json.encodeToString(StoredRatesData.serializer(), storedRatesData)
        )
    }

}
