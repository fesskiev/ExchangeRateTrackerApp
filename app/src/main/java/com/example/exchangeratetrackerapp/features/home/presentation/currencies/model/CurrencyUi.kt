package com.example.exchangeratetrackerapp.features.home.presentation.currencies.model

data class CurrencyUi(
    val code: String,
    val name: String,
    val symbol: String,
    var isSelected: Boolean
)