package com.gws.auto.mobile.android.ui.trigger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gws.auto.mobile.android.databinding.FragmentTriggerBinding
import timber.log.Timber

class TriggerFragment : Fragment() {

    private var _binding: FragmentTriggerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTriggerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
