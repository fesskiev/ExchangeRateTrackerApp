package com.example.exchangeratetrackerapp.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.exchangeratetrackerapp.BuildConfig
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

suspend fun <T> DataStore<Preferences>.getOnes(key: Preferences.Key<T>) =
    get(key).firstOrNull()

fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>) = data
    .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
    .map { it[key] }

suspend fun <T> DataStore<Preferences>.put(key: Preferences.Key<T>, value: T) {
    edit {
        it[key] = value
    }
}

private const val PREFERENCE_NAME = "prefs"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "${BuildConfig.APPLICATION_ID}$PREFERENCE_NAME"
)