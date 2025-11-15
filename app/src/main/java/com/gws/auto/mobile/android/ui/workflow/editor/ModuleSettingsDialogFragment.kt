package com.gws.auto.mobile.android.ui.workflow.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gws.auto.mobile.android.databinding.FragmentModuleSettingsBinding
import com.gws.auto.mobile.android.domain.model.Module

class ModuleSettingsDialogFragment(private val module: Module) : DialogFragment() {

    private var _binding: FragmentModuleSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.moduleType.text = module.type
        // This is a placeholder for dynamic parameter views
        binding.parametersContainer.removeAllViews()
        module.parameters.forEach { (key, value) ->
            val textView = android.widget.TextView(requireContext()).apply {
                text = "$key: $value"
            }
            binding.parametersContainer.addView(textView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
