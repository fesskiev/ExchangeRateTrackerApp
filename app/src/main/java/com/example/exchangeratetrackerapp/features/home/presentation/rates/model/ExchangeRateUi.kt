package com.example.exchangeratetrackerapp.features.home.presentation.rates.model

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateUi(
    val code: String,
    val rate: Double,
    val percentChange: Double
)