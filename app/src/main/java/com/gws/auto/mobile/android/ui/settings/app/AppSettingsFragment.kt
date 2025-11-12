package com.gws.auto.mobile.android.ui.settings.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentAppSettingsBinding
import com.gws.auto.mobile.android.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppSettingsFragment : Fragment() {

    private var _binding: FragmentAppSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupSpinners()
    }

    private fun setupNavigation() {
        binding.userInfoButton.setOnClickListener { startSettingsActivity("user_info") }
        binding.accountConnectionsButton.setOnClickListener { startSettingsActivity("account_connections") }
        binding.tagManagementButton.setOnClickListener { startSettingsActivity("tag_management") }
    }

    private fun startSettingsActivity(fragmentKey: String) {
        val intent = Intent(activity, SettingsActivity::class.java).apply {
            putExtra("fragment_to_load", fragmentKey)
        }
        startActivity(intent)
    }

    private fun setupSpinners() {
        // First day of week
        val firstDayOfWeekAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.first_day_of_week_entries,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.firstDayOfWeekSpinner.adapter = it
        }

        val currentFirstDay = prefs.getString("first_day_of_week", "Sunday")
        val firstDayPosition = firstDayOfWeekAdapter.getPosition(currentFirstDay)
        binding.firstDayOfWeekSpinner.setSelection(firstDayPosition)

        binding.firstDayOfWeekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = parent.getItemAtPosition(position) as String
                prefs.edit { putString("first_day_of_week", selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Country for holidays
        val countryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.country_entries,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.countrySpinner.adapter = it
        }

        val countryValues = resources.getStringArray(R.array.country_values)
        val currentCountry = prefs.getString("country_for_holidays", "US")
        val countryPosition = countryValues.indexOf(currentCountry)
        binding.countrySpinner.setSelection(if (countryPosition != -1) countryPosition else 0)

        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = countryValues[position]
                prefs.edit { putString("country_for_holidays", selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Language
        val languageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_entries,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.languageSpinner.adapter = it
        }

        val languageValues = resources.getStringArray(R.array.language_values)
        val currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val langPosition = languageValues.indexOf(currentLang.ifEmpty { "en" })
        binding.languageSpinner.setSelection(if (langPosition != -1) langPosition else 0)

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLang = languageValues[position]
                val appLocale = LocaleListCompat.forLanguageTags(selectedLang)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Theme
        val themeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.theme_entries,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.themeSpinner.adapter = it
        }

        val themeValues = resources.getStringArray(R.array.theme_values)
        val currentTheme = prefs.getString("theme", "System")
        val themePosition = themeValues.indexOf(currentTheme)
        binding.themeSpinner.setSelection(if (themePosition != -1) themePosition else 0)

        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = themeValues[position]
                val currentTheme = prefs.getString("theme", "System")
                if (selection != currentTheme) {
                    prefs.edit { putString("theme", selection) }
                    val mode = when (selection) {
                        "Light" -> AppCompatDelegate.MODE_NIGHT_NO
                        "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
