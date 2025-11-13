package com.gws.auto.mobile.android.data.repository

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(private val prefs: SharedPreferences) {

    val highlightColor: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_HIGHLIGHT_COLOR) {
                trySend(prefs.getString(key, "default") ?: "default")
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        // Send initial value
        trySend(prefs.getString(PREF_HIGHLIGHT_COLOR, "default") ?: "default")
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    companion object {
        const val PREF_HIGHLIGHT_COLOR = "highlight_color"
    }
}
