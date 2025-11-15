package com.gws.auto.mobile.android.data.repository

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(private val prefs: SharedPreferences) {

    val theme: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_THEME) {
                trySend(prefs.getString(key, "System") ?: "System")
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        // Emit initial value
        trySend(prefs.getString(PREF_THEME, "System") ?: "System")
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    val highlightColor: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_HIGHLIGHT_COLOR) {
                trySend(prefs.getString(key, "default") ?: "default")
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(PREF_HIGHLIGHT_COLOR, "default") ?: "default")
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    val firstDayOfWeek: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_FIRST_DAY_OF_WEEK) {
                trySend(prefs.getString(key, "Sunday") ?: "Sunday")
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(PREF_FIRST_DAY_OF_WEEK, "Sunday") ?: "Sunday")
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    val country: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_COUNTRY) {
                trySend(prefs.getString(key, "US") ?: "US")
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(PREF_COUNTRY, "US") ?: "US")
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    companion object {
        const val PREF_THEME = "theme"
        const val PREF_HIGHLIGHT_COLOR = "highlight_color"
        const val PREF_FIRST_DAY_OF_WEEK = "first_day_of_week"
        const val PREF_COUNTRY = "country"
    }
}
