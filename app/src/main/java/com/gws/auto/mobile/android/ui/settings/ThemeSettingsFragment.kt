package com.gws.auto.mobile.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.gws.auto.mobile.android.databinding.FragmentThemeSettingsBinding

class ThemeSettingsFragment : Fragment() {

    private var _binding: FragmentThemeSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        when (prefs.getString("theme", "System")) {
            "Light" -> binding.themeLightRadio.isChecked = true
            "Dark" -> binding.themeDarkRadio.isChecked = true
            else -> binding.themeSystemRadio.isChecked = true
        }

        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                binding.themeLightRadio.id -> "Light"
                binding.themeDarkRadio.id -> "Dark"
                else -> "System"
            }
            prefs.edit().putString("theme", theme).apply()
            updateTheme(theme)
        }
    }

    private fun updateTheme(theme: String) {
        when (theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
