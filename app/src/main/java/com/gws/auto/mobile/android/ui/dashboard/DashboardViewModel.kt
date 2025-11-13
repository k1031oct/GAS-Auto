package com.gws.auto.mobile.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        getStatsForCurrentMonth(),
        getStatsForPreviousMonth(),
        historyRepository.getWorkflowExecutionCounts(),
        workflowRepository.getAllWorkflows(),
        historyRepository.getAllHistory()
    ) { currentMonthStats, previousMonthStats, workflowCounts, allWorkflows, allHistory ->
        val totalCountChange = calculateChange(currentMonthStats.totalCount, previousMonthStats.totalCount)
        val errorCountChange = calculateChange(currentMonthStats.errorCount, previousMonthStats.errorCount)
        val totalDurationChange = calculateChange(currentMonthStats.totalDuration, previousMonthStats.totalDuration)
        val moduleStats = calculateModuleStats(allWorkflows, allHistory)

        DashboardUiState(
            totalCount = currentMonthStats.totalCount,
            errorCount = currentMonthStats.errorCount,
            totalDuration = currentMonthStats.totalDuration,
            totalCountChange = totalCountChange,
            errorCountChange = errorCountChange,
            totalDurationChange = totalDurationChange,
            workflowExecutionCounts = workflowCounts,
            moduleStats = moduleStats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = DashboardUiState()
    )

    private fun getStatsForCurrentMonth() = getStatsForMonth(LocalDate.now())

    private fun getStatsForPreviousMonth() = getStatsForMonth(LocalDate.now().minusMonths(1))

    private fun getStatsForMonth(date: LocalDate) = historyRepository.getStatsForPeriod(
        date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        date.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    private fun calculateChange(current: Int, previous: Int): Float {
        return if (previous == 0) 0f else (current - previous) / previous.toFloat() * 100
    }

    private fun calculateChange(current: Long, previous: Long): Float {
        return if (previous == 0L) 0f else (current - previous) / previous.toFloat() * 100
    }

    private fun calculateModuleStats(workflows: List<Workflow>, history: List<History>): List<ModuleStat> {
        val moduleUsage = mutableMapOf<String, Int>()
        val moduleErrors = mutableMapOf<String, Int>()

        for (h in history) {
            val workflow = workflows.find { it.id == h.workflowId }
            workflow?.modules?.forEach { module ->
                moduleUsage[module.type] = (moduleUsage[module.type] ?: 0) + 1
                if (h.status == "Failure") { // A simplified assumption
                    moduleErrors[module.type] = (moduleErrors[module.type] ?: 0) + 1
                }
            }
        }

        return moduleUsage.map { (name, usage) ->
            ModuleStat(name, usage, moduleErrors.getOrDefault(name, 0))
        }.sortedByDescending { it.usageCount }
    }
}
