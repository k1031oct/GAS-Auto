package com.gws.auto.mobile.android.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gws.auto.mobile.android.data.local.db.IntListConverter
import com.gws.auto.mobile.android.data.local.db.ListConverter

@Entity(tableName = "schedules")
@TypeConverters(ListConverter::class, IntListConverter::class)
data class Schedule(
    @PrimaryKey
    val id: String,
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
