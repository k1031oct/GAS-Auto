package com.gws.auto.mobile.android.ui.wizard

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val prefs: SharedPreferences
) : ViewModel() {

    fun setLanguage(language: String) {
        prefs.edit { putString("language", language) }
        val localeTag = when (language) {
            "Japanese" -> "ja"
            "Chinese" -> "zh"
            "Korean" -> "ko"
            else -> "en"
        }
        val appLocale = LocaleListCompat.forLanguageTags(localeTag)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun setCountry(countryCode: String) {
        prefs.edit { putString("country", countryCode) }
    }

    fun setWeekStart(day: String) {
        prefs.edit { putString("first_day_of_week", day) }
    }

    fun setTheme(theme: String) {
        prefs.edit { putString("theme", theme) }
        when (theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun getTheme(): String {
        return prefs.getString("theme", "Default") ?: "Default"
    }

    fun finishWizard() {
        prefs.edit { putBoolean("is_first_run", false) }
    }
}
