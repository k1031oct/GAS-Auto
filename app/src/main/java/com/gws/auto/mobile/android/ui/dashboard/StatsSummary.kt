package com.gws.auto.mobile.android.ui.dashboard

import androidx.room.ColumnInfo

data class StatsSummary(
    @ColumnInfo(name = "total_count") val totalCount: Int,
    @ColumnInfo(name = "error_count") val errorCount: Int,
    @ColumnInfo(name = "total_duration") val totalDuration: Long
)
