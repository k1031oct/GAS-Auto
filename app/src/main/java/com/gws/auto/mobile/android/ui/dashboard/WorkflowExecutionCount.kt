package com.gws.auto.mobile.android.ui.dashboard

import androidx.room.ColumnInfo

data class WorkflowExecutionCount(
    @ColumnInfo(name = "workflowName") val workflowName: String,
    @ColumnInfo(name = "executionCount") val executionCount: Int
)
