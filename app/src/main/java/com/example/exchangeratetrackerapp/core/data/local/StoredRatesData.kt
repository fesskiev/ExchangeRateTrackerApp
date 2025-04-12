package com.example.exchangeratetrackerapp.core.data.local

import com.example.exchangeratetrackerapp.core.common.format
import com.example.exchangeratetrackerapp.core.data.remote.ExchangeRateResponse
import kotlinx.serialization.Serializable

@Serializable
data class StoredRatesData(
    val updateTime: String,
    val rates: Map<String, Double>
)

fun ExchangeRateResponse.mapResponseToStoredRatesData() =
    StoredRatesData(
        updateTime = System.currentTimeMillis().format(),
        rates = rates
    )
