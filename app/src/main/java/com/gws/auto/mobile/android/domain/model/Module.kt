package com.gws.auto.mobile.android.domain.model

/**
 * A single executable task within a workflow.
 */
data class Module(
    val id: String,
    val type: String, // e.g., "CREATE_PDF_FROM_SHEET"
    val parameters: Map<String, String> // e.g., {"sheet_url": "https://...", "output_name": "report.pdf"}
)
