package com.example.exchangeratetrackerapp.core.data.local

import kotlinx.serialization.Serializable

@Serializable
data class StoredRatesData(
    val updateTime: String,
    val rates: Map<String, Double>
)