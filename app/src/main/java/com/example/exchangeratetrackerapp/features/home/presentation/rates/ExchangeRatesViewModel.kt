package com.example.exchangeratetrackerapp.features.home.presentation.rates

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ExchangeRatesViewModel : ViewModel() {

    val state = MutableStateFlow(ExchangeRatesState())
        private set
}