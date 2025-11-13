package com.gws.auto.mobile.android.ui.dashboard

data class DashboardUiState(
    // Monthly Stats
    val totalCountMonth: Int = 0,
    val errorCountMonth: Int = 0,
    val totalDurationMonth: Long = 0,
    val totalCountMonthChange: Float = 0f,
    val errorCountMonthChange: Float = 0f,
    val totalDurationMonthChange: Float = 0f,

    // Daily Stats
    val totalCountDay: Int = 0,
    val errorCountDay: Int = 0,
    val totalDurationDay: Long = 0,
    val totalCountDayChange: Float = 0f,
    val errorCountDayChange: Float = 0f,
    val totalDurationDayChange: Float = 0f,

    // Workflow Stats
    val workflowExecutionCounts: List<WorkflowExecutionCount> = emptyList(),

    // Module Stats
    val moduleUsageCount: Int = 0,
    val moduleErrorCount: Int = 0,
    val moduleStats: List<ModuleStat> = emptyList()
)
