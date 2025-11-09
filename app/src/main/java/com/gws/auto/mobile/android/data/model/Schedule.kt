package com.gws.auto.mobile.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val id: String = "",
    val workflowId: String = "",
    val scheduleType: String = "daily", // hourly, daily, weekly, monthly, yearly
    val hourlyInterval: Int? = null,
    val time: String? = null, // "HH:mm"
    val weeklyDays: List<String>? = null,
    val monthlyDays: List<Int>? = null,
    val yearlyMonth: Int? = null,
    val yearlyDayOfMonth: Int? = null,
    val lastRun: Long? = null,
    val nextRun: Long? = null,
    val isEnabled: Boolean = true
)
