package com.gws.auto.mobile.android.ui.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentWizardWeekStartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeekStartFragment : Fragment() {

    private var _binding: FragmentWizardWeekStartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WizardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWizardWeekStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.weekStartGroup.setOnCheckedChangeListener { _, checkedId ->
            val selection = if (checkedId == R.id.monday_button) "Monday" else "Sunday"
            viewModel.setWeekStart(selection)
        }
        // Set a default value
        binding.sundayButton.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
