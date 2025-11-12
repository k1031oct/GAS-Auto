package com.gws.auto.mobile.android.domain.model

import java.util.Date

data class WorkflowExecutionLog(
    val id: String,
    val workflowName: String,
    val executionTime: Date,
    val status: String, // e.g., "Success", "Failure"
    val durationMs: Long
)
