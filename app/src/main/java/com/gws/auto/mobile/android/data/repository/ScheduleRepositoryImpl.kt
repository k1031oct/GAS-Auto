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
import timber.log.Timber
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
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
            val calendarId = when (country) {
                "US" -> "en.usa#holiday@group.v.calendar.google.com"
                "JP" -> "ja.japanese#holiday@group.v.calendar.google.com"
                "CN" -> "zh.china#holiday@group.v.calendar.google.com"
                "KR" -> "ko.south_korea#holiday@group.v.calendar.google.com"
                else -> "en.usa#holiday@group.v.calendar.google.com" // Default to US
            }

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
                val dateString = event.start.date?.toString() ?: event.start.dateTime?.toStringRfc3339()?.substring(0, 10)
                val eventDate = dateString?.let { LocalDate.parse(it) }

                if (eventDate != null) {
                    Holiday(name = event.summary, date = eventDate)
                } else {
                    null
                }
            }
        } catch (e: IOException) {
            Timber.e(e, "Failed to fetch holidays for $country, $year-$month")
            emptyList()
        } catch (e: Exception) {
            Timber.e(e, "An unexpected error occurred while fetching holidays for $country, $year-$month")
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
