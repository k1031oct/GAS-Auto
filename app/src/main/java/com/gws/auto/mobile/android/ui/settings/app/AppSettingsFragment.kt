package com.gws.auto.mobile.android.ui.settings.app

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
import com.gws.auto.mobile.android.data.repository.UserPreferencesRepository
import com.gws.auto.mobile.android.databinding.FragmentAppSettingsBinding
import com.gws.auto.mobile.android.ui.settings.tag.TagManagementFragment
import com.gws.auto.mobile.android.ui.settings.user.UserInfoFragment
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
        setupListeners()
        setupSpinners()
    }

    private fun setupListeners() {
        binding.userInfoButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, UserInfoFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.tagManagementButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, TagManagementFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupSpinners() {
        // First day of week
        val firstDayOfWeekAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.first_day_of_week_entries,
            R.layout.spinner_item_right_aligned
        ).also {
            it.setDropDownViewResource(R.layout.spinner_item_right_aligned)
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
            R.layout.spinner_item_right_aligned
        ).also {
            it.setDropDownViewResource(R.layout.spinner_item_right_aligned)
            binding.countrySpinner.adapter = it
        }

        val countryValues = resources.getStringArray(R.array.country_values)
        val currentCountry = prefs.getString("country", "US")
        val countryPosition = countryValues.indexOf(currentCountry)
        binding.countrySpinner.setSelection(if (countryPosition != -1) countryPosition else 0)

        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = countryValues[position]
                prefs.edit { putString("country", selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Language
        val languageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_entries,
            R.layout.spinner_item_right_aligned
        ).also {
            it.setDropDownViewResource(R.layout.spinner_item_right_aligned)
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
            R.layout.spinner_item_right_aligned
        ).also {
            it.setDropDownViewResource(R.layout.spinner_item_right_aligned)
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

        // Highlight Color
        val highlightColorAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.highlight_color_entries,
            R.layout.spinner_item_right_aligned
        ).also {
            it.setDropDownViewResource(R.layout.spinner_item_right_aligned)
            binding.highlightColorSpinner.adapter = it
        }

        val highlightColorValues = resources.getStringArray(R.array.highlight_color_values)
        val currentHighlightColor = prefs.getString(UserPreferencesRepository.PREF_HIGHLIGHT_COLOR, "default")
        val highlightColorPosition = highlightColorValues.indexOf(currentHighlightColor)
        binding.highlightColorSpinner.setSelection(if (highlightColorPosition != -1) highlightColorPosition else 0)

        binding.highlightColorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selection = highlightColorValues[position]
                prefs.edit { putString(UserPreferencesRepository.PREF_HIGHLIGHT_COLOR, selection) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
