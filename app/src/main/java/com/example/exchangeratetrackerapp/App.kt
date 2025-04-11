package com.example.exchangeratetrackerapp

import android.app.Application
import com.example.exchangeratetrackerapp.core.data.di.dataModule
import com.example.exchangeratetrackerapp.features.home.di.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            koinModules()
        }
    }

    private fun KoinApplication.koinModules() =
        modules(
            dataModule,
            homeModule
        )
}