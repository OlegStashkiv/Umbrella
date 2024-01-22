package com.olegstashkiv.umbrella.data.db

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveLocation(location: String) {
        dataStore.edit { preferences ->
            preferences[LOCATION_KEY] = location
        }
    }

    suspend fun getLocation(): String {
        val preferences = dataStore.data.first()
        return preferences[LOCATION_KEY] ?: ""
    }

    suspend fun removeLocation() {
        dataStore.edit { preferences ->
            preferences.remove(LOCATION_KEY)
        }
    }

    companion object {
        private val LOCATION_KEY = stringPreferencesKey("location_key")
    }
}