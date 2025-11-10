package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.domain.model.Holiday
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedulesFlow(): Flow<List<Schedule>>
    suspend fun addSchedule(schedule: Schedule)
    suspend fun updateSchedule(schedule: Schedule)
    suspend fun deleteSchedule(scheduleId: String)
    suspend fun getHolidays(countryCode: String, year: Int): List<Holiday>
}
