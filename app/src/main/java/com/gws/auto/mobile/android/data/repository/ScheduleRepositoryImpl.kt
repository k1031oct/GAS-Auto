package com.gws.auto.mobile.android.data.repository

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.gws.auto.mobile.android.data.local.db.ScheduleDao
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import java.util.TimeZone
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val googleApiAuthorizer: GoogleApiAuthorizer
) : ScheduleRepository {

    override fun getSchedulesFlow(): Flow<List<Schedule>> = scheduleDao.getAllSchedules()

    override suspend fun addSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule)
    }

    override suspend fun getHolidays(country: String, year: Int, month: Int): List<Holiday> = withContext(Dispatchers.IO) {
        try {
            val calendarService = getCalendarService()
            val calendarId = "en.$country#holiday@group.v.calendar.google.com"

            val startOfMonth = LocalDate.of(year, month, 1)
            val endOfMonth = startOfMonth.plusMonths(1)

            val timeMin = DateTime(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            val timeMax = DateTime(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())

            val events = calendarService.events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()

            events.items.mapNotNull { event ->
                val eventDate = event.start.date?.value?.let { LocalDate.parse(it.toString()) } ?: return@mapNotNull null
                Holiday(name = event.summary, date = eventDate)
            }
        } catch (e: IOException) {
            // Consider logging the exception or handling it more gracefully
            emptyList()
        }
    }

    private fun getCalendarService(): Calendar {
        val credential = googleApiAuthorizer.getCredential(listOf("https://www.googleapis.com/auth/calendar.readonly"))
        return Calendar.Builder(googleApiAuthorizer.httpTransport, googleApiAuthorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }
}
