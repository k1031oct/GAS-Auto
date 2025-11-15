package com.gws.auto.mobile.android.data.repository

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
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
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val googleApiAuthorizer: GoogleApiAuthorizer
) : ScheduleRepository {

    override fun getSchedulesFlow(): Flow<List<Schedule>> {
        return scheduleDao.getAllSchedules()
    }

    override suspend fun addSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule)
    }

    override suspend fun getHolidays(country: String, year: Int): List<Holiday> {
        return withContext(Dispatchers.IO) {
            try {
                val calendar = getCalendarService()
                val calendarId = "en.$country#holiday@group.v.calendar.google.com"

                val timeMin = DateTime(Date.from(LocalDate.of(year, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                val timeMax = DateTime(Date.from(LocalDate.of(year, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()))

                val events = calendar.events().list(calendarId)
                    .setTimeMin(timeMin)
                    .setTimeMax(timeMax)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()

                events.items.mapNotNull { event ->
                    val dateStr = event.start?.date?.toStringRfc3339() ?: return@mapNotNull null
                    val localDate = LocalDate.parse(dateStr)
                    Holiday(localDate, event.summary)
                }
            } catch (e: IOException) {
                // Handle API errors, e.g., by logging or returning an empty list
                emptyList()
            }
        }
    }

    private fun getCalendarService(): Calendar {
        val credential = googleApiAuthorizer.getCredential(listOf(CalendarScopes.CALENDAR_READONLY))
        return Calendar.Builder(googleApiAuthorizer.httpTransport, googleApiAuthorizer.jsonFactory, credential)
            .setApplicationName("GWS Auto for Android")
            .build()
    }
}
