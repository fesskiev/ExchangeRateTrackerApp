package com.example.exchangeratetrackerapp.core.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AppIdInterceptor(private val appId: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .url(chain.request().url.newBuilder().addQueryParameter("app_id", appId).build())
                .build()
        return chain.proceed(request)
    }
}