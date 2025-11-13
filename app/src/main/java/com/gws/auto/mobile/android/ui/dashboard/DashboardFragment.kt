package com.gws.auto.mobile.android.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var moduleStatAdapter: ModuleStatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupCharts() {
        // Common chart setup
        binding.errorRateChart.description.isEnabled = false
        binding.workflowRankingChart.description.isEnabled = false
    }

    private fun setupRecyclerView() {
        moduleStatAdapter = ModuleStatAdapter()
        binding.moduleStatsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moduleStatAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { uiState ->
                updateSummary(uiState)
                updateErrorRateChart(uiState)
                updateWorkflowRankingChart(uiState)
                moduleStatAdapter.submitList(uiState.moduleStats)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateSummary(uiState: DashboardUiState) {
        binding.totalExecutionsText.text = getString(R.string.dashboard_total_executions, uiState.totalCount)
        binding.errorCountText.text = getString(R.string.dashboard_error_count, uiState.errorCount)
        binding.totalDurationText.text = formatDuration(uiState.totalDuration)

        binding.totalExecutionsChangeText.text = formatChange(uiState.totalCountChange)
        binding.errorCountChangeText.text = formatChange(uiState.errorCountChange)
        binding.totalDurationChangeText.text = formatChange(uiState.totalDurationChange)
    }

    private fun updateErrorRateChart(uiState: DashboardUiState) {
        if (uiState.totalCount == 0) {
            binding.errorRateChart.visibility = View.GONE
            return
        }
        binding.errorRateChart.visibility = View.VISIBLE

        val successCount = uiState.totalCount - uiState.errorCount
        val entries = listOf(
            PieEntry(successCount.toFloat(), getString(R.string.execution_status_success)),
            PieEntry(uiState.errorCount.toFloat(), getString(R.string.execution_status_failure))
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.chart_green),
                ContextCompat.getColor(requireContext(), R.color.chart_red)
            )
            setDrawValues(true)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        binding.errorRateChart.apply {
            data = PieData(dataSet)
            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 61f
            centerText = "Error Rate"
            setCenterTextSize(16f)
            legend.isEnabled = false
            animateY(1400)
            invalidate()
        }
    }

    private fun updateWorkflowRankingChart(uiState: DashboardUiState) {
        if (uiState.workflowExecutionCounts.isEmpty()) {
            binding.workflowRankingChart.visibility = View.GONE
            return
        }
        binding.workflowRankingChart.visibility = View.VISIBLE

        val entries = uiState.workflowExecutionCounts.mapIndexed { index, count ->
            BarEntry(index.toFloat(), count.executionCount.toFloat())
        }
        val labels = uiState.workflowExecutionCounts.map { it.workflowName }

        val dataSet = BarDataSet(entries, "Executions").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            setDrawValues(true)
            valueTextSize = 10f
        }

        binding.workflowRankingChart.apply {
            data = BarData(dataSet)
            setFitBars(true)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(labels)
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
            }
            axisLeft.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
            }
            axisRight.isEnabled = false
            legend.isEnabled = true
            animateY(1400)
            invalidate()
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        return "Time: ${hours}h"
    }

    private fun formatChange(change: Float): String {
        val sign = if (change >= 0) "+" else ""
        return String.format("$sign%.1f%%", change)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
