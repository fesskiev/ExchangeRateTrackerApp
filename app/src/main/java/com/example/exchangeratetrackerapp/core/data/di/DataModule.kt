package com.example.exchangeratetrackerapp.core.data.di

import com.example.exchangeratetrackerapp.BuildConfig
import com.example.exchangeratetrackerapp.core.data.local.CurrenciesCache
import com.example.exchangeratetrackerapp.core.data.local.CurrenciesCacheImpl
import com.example.exchangeratetrackerapp.core.data.local.ExchangeRatesCache
import com.example.exchangeratetrackerapp.core.data.local.ExchangeRatesCacheImpl
import com.example.exchangeratetrackerapp.core.data.local.dataStore
import com.example.exchangeratetrackerapp.core.data.remote.AppIdInterceptor
import com.example.exchangeratetrackerapp.core.data.remote.ExchangeRateApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext

val dataModule = module {

    single {
        buildApiService(
            retrofit = get()
        )
    }

    single {
        buildRetrofit(
            okHttpClient = get()
        )
    }

    single {
        buildOkHttpClient()
    }

    single<ExchangeRatesCache> {
        ExchangeRatesCacheImpl(
            dataStore = androidContext().dataStore,
            json = Json
        )
    }

    single<CurrenciesCache> {
        CurrenciesCacheImpl(
            dataStore = androidContext().dataStore,
            json = Json
        )
    }
}

private fun buildApiService(
    retrofit: Retrofit,
): ExchangeRateApi {
    return retrofit.create(ExchangeRateApi::class.java)
}

private fun buildOkHttpClient(): OkHttpClient {
    val timeOut = 60L
    return OkHttpClient.Builder().apply {
        readTimeout(timeOut, TimeUnit.SECONDS)
        writeTimeout(timeOut, TimeUnit.SECONDS)
        connectTimeout(timeOut, TimeUnit.SECONDS)
        addInterceptor(AppIdInterceptor(BuildConfig.APP_ID))
        if (BuildConfig.DEBUG) {
            val logging =
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            addInterceptor(logging)
        }
    }.build()
}

private fun buildRetrofit(
    okHttpClient: OkHttpClient,
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()
}