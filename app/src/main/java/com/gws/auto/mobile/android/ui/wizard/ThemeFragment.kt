package com.gws.auto.mobile.android.ui.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentWizardThemeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeFragment : Fragment() {

    private var _binding: FragmentWizardThemeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WizardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWizardThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Set initial state without triggering listener
        updateInitialThemeSelection()

        // 2. Set listener for user interaction
        binding.themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val selection = when (checkedId) {
                R.id.light_button -> "Light"
                R.id.dark_button -> "Dark"
                else -> "Default"
            }
            viewModel.setTheme(selection)
        }
    }

    private fun updateInitialThemeSelection() {
        // Temporarily disable the listener to prevent loops
        binding.themeGroup.setOnCheckedChangeListener(null)
        when (viewModel.getTheme()) {
            "Light" -> binding.lightButton.isChecked = true
            "Dark" -> binding.darkButton.isChecked = true
            else -> binding.systemButton.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
