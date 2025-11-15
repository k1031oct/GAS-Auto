package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedulesFlow(): Flow<List<Schedule>>
    suspend fun addSchedule(schedule: Schedule)
    suspend fun updateSchedule(schedule: Schedule)
    suspend fun getHolidays(country: String, year: Int): List<Holiday>
}
