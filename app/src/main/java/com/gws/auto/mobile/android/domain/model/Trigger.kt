package com.gws.auto.mobile.android.domain.model

/**
 * Defines the condition that starts a workflow.
 */
data class Trigger(
    val id: String,
    val workflowId: String,
    val type: String, // e.g., "SCHEDULED", "MANUAL"
    val configuration: Map<String, String> // e.g., {"cron": "0 9 * * *"} for a scheduled trigger
)
