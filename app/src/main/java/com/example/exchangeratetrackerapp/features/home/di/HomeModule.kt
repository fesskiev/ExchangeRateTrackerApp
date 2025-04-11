package com.example.exchangeratetrackerapp.features.home.di

import com.example.exchangeratetrackerapp.features.home.presentation.currencies.AddCurrenciesViewModel
import com.example.exchangeratetrackerapp.features.home.presentation.rates.ExchangeRatesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {

    viewModelOf(::ExchangeRatesViewModel)
    viewModelOf(::AddCurrenciesViewModel)
}