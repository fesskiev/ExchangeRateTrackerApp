package com.example.exchangeratetrackerapp.core.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
)