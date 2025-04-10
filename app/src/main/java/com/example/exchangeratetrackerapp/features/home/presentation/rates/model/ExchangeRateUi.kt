package com.example.exchangeratetrackerapp.features.home.presentation.rates.model

data class ExchangeRateUi(
    val code: String,
    val rate: Double,
    val timestamp: Long,
    val percentChange: Double
)