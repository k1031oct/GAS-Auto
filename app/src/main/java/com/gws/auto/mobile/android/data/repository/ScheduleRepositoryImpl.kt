package com.gws.auto.mobile.android.data.repository

import com.google.api.client.util.DateTime
import com.gws.auto.mobile.android.data.local.db.ScheduleDao
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

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

    override suspend fun deleteSchedule(scheduleId: String) {
        scheduleDao.deleteScheduleById(scheduleId)
    }

    override suspend fun getHolidays(countryCode: String, year: Int): List<Holiday> = withContext(Dispatchers.IO) {
        try {
            val calendar = googleApiAuthorizer.getCalendarClient()
            if (calendar == null) {
                Timber.w("Failed to get Calendar client. User might not be authenticated for Google APIs.")
                return@withContext emptyList()
            }

            val calendarId = getHolidayCalendarId(countryCode)
            val timeMin = DateTime(Date.from(LocalDate.of(year, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
            val timeMax = DateTime(Date.from(LocalDate.of(year, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()))

            val events = calendar.events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setSingleEvents(true)
                .execute()

            return@withContext events.items.mapNotNull { event ->
                val dateString = event.start?.date?.toString()
                if (dateString != null) {
                    Holiday(LocalDate.parse(dateString), event.summary)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch holidays for country: $countryCode")
            return@withContext emptyList()
        }
    }

    private fun getHolidayCalendarId(countryCode: String): String {
        return when (countryCode.uppercase(Locale.US)) {
            "US" -> "en.usa#holiday@group.v.calendar.google.com"
            "JP" -> "ja.japanese#holiday@group.v.calendar.google.com"
            "CN" -> "zh.china#holiday@group.v.calendar.google.com"
            "KR" -> "ko.south_korea#holiday@group.v.calendar.google.com"
            // Add other countries as needed
            else -> "en.usa#holiday@group.v.calendar.google.com" // Default to USA
        }
    }
}
