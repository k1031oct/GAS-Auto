package com.gws.auto.mobile.android.ui.wizard

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun setLanguage(language: String) {
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
        viewModelScope.launch {
            settingsRepository.saveCountry(countryCode)
        }
    }

    fun setWeekStart(day: String) {
        viewModelScope.launch {
            settingsRepository.saveFirstDayOfWeek(day)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.saveTheme(theme)
        }
        when (theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun finishWizard() {
        // This should probably be handled by a different mechanism
        // but for now, we leave it as it is.
    }
}
