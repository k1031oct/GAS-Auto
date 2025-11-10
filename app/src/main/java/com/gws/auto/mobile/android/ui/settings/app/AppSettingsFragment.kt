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
import com.google.android.material.chip.Chip
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentAppSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppSettingsFragment : Fragment() {

    private var _binding: FragmentAppSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefs: SharedPreferences

    private val tags = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupTagEditor()
        loadTags()
    }

    private fun setupSpinners() {
        // First day of week spinner
        val firstDayOfWeekAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.first_day_of_week_entries,
            android.R.layout.simple_spinner_item
        )
        firstDayOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.firstDayOfWeekSpinner.adapter = firstDayOfWeekAdapter
        val firstDayOfWeek = prefs.getString("first_day_of_week", "Sunday")
        val firstDayOfWeekPosition = firstDayOfWeekAdapter.getPosition(firstDayOfWeek)
        binding.firstDayOfWeekSpinner.setSelection(firstDayOfWeekPosition)
        binding.firstDayOfWeekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                prefs.edit { putString("first_day_of_week", parent.getItemAtPosition(position).toString()) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Country spinner
        val countryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.country_entries,
            android.R.layout.simple_spinner_item
        )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.adapter = countryAdapter
        val country = prefs.getString("country", "US")
        val countryValues = resources.getStringArray(R.array.country_values)
        val countryPosition = countryValues.indexOf(country)
        binding.countrySpinner.setSelection(countryPosition)
        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                prefs.edit { putString("country", countryValues[position]) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Language spinner
        val languageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_entries,
            android.R.layout.simple_spinner_item
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = languageAdapter
        val language = prefs.getString("language", "English")
        val languagePosition = languageAdapter.getPosition(language)
        binding.languageSpinner.setSelection(languagePosition)
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                val currentLanguage = prefs.getString("language", "English")
                if (selectedLanguage != currentLanguage) {
                    prefs.edit { putString("language", selectedLanguage) }
                    val locale = when (selectedLanguage) {
                        "Japanese" -> "ja"
                        "Chinese" -> "zh"
                        "Korean" -> "ko"
                        else -> "en"
                    }
                    val appLocale = LocaleListCompat.forLanguageTags(locale)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Theme spinner
        val themeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.theme_entries,
            android.R.layout.simple_spinner_item
        )
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.themeSpinner.adapter = themeAdapter
        val theme = prefs.getString("theme", "Default")
        val themePosition = themeAdapter.getPosition(theme)
        binding.themeSpinner.setSelection(themePosition)
        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTheme = parent.getItemAtPosition(position).toString()
                val currentTheme = prefs.getString("theme", "Default")
                if (selectedTheme != currentTheme) {
                    prefs.edit { putString("theme", selectedTheme) }
                    when (selectedTheme) {
                        "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupTagEditor() {
        binding.addTagButton.setOnClickListener {
            val tagText = binding.tagEditor.text.toString().trim()
            if (tagText.isNotEmpty() && tags.add(tagText)) {
                addChipToGroup(tagText)
                saveTags()
                binding.tagEditor.text.clear()
            }
        }
    }

    private fun loadTags() {
        tags.clear()
        tags.addAll(prefs.getStringSet("workflow_tags", emptySet()) ?: emptySet())
        binding.tagChipGroup.removeAllViews()
        tags.forEach { addChipToGroup(it) }
    }

    private fun saveTags() {
        prefs.edit { putStringSet("workflow_tags", tags) }
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(requireContext()).apply {
            text = tag
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                binding.tagChipGroup.removeView(this)
                tags.remove(tag)
                saveTags()
            }
        }
        binding.tagChipGroup.addView(chip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
