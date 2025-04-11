package com.example.exchangeratetrackerapp.core.data.remote

import retrofit2.http.GET

interface ExchangeRateApi {

    @GET("latest.json")
    suspend fun getLatestExchangeRates(): ExchangeRateResponse

    @GET("currencies.json")
    suspend fun getCurrencies(): CurrencyResponse
}