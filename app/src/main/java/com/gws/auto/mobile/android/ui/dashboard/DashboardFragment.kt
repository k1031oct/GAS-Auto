package com.gws.auto.mobile.android.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.gws.auto.mobile.android.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.stats
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { stats ->
                updateDashboardGrid(stats)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateDashboardGrid(stats: List<DashboardStat>) {
        binding.dashboardGrid.removeAllViews()
        stats.forEach { stat ->
            val card = createStatCard(stat)
            binding.dashboardGrid.addView(card)
        }
    }

    private fun createStatCard(stat: DashboardStat): MaterialCardView {
        val card = MaterialCardView(requireContext())
        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
            setMargins(0, 0, 0, 0)
        }
        card.layoutParams = params
        card.elevation = 0f
        card.setCardBackgroundColor(MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorSurfaceVariant, Color.GRAY))

        val linearLayout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(requireContext()).apply {
            text = stat.title
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium)
        }
        val value = TextView(requireContext()).apply {
            text = stat.value
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_DisplayMedium)
        }

        linearLayout.addView(title)
        linearLayout.addView(value)
        card.addView(linearLayout)
        return card
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
