package com.gws.auto.mobile.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val FIRST_DAY_OF_WEEK = stringPreferencesKey("first_day_of_week")
    }

    val firstDayOfWeek: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FIRST_DAY_OF_WEEK] ?: "Sunday"
        }

    suspend fun saveFirstDayOfWeek(firstDayOfWeek: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.FIRST_DAY_OF_WEEK] = firstDayOfWeek
        }
    }
}
