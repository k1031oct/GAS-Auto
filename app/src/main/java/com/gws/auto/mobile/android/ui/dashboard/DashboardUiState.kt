package com.gws.auto.mobile.android.ui.dashboard

data class DashboardUiState(
    val totalCount: Int = 0,
    val errorCount: Int = 0,
    val totalDuration: Long = 0,
    val totalCountChange: Float = 0f,
    val errorCountChange: Float = 0f,
    val totalDurationChange: Float = 0f,
    val workflowExecutionCounts: List<WorkflowExecutionCount> = emptyList(),
    val moduleStats: List<ModuleStat> = emptyList()
)
