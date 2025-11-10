package com.gws.auto.mobile.android.data.repository

import com.google.api.client.util.DateTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val googleApiAuthorizer: GoogleApiAuthorizer
) : ScheduleRepository {

    private val userId: String?
        get() = auth.currentUser?.uid

    private fun schedulesCollection() = userId?.let { firestore.collection("users/$it/schedules") }

    override fun getSchedulesFlow(): Flow<List<Schedule>> = callbackFlow {
        val listenerRegistration = schedulesCollection()?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val schedules = snapshot?.documents?.mapNotNull { it.toObject(Schedule::class.java) } ?: emptyList()
            trySend(schedules)
        }
        awaitClose { listenerRegistration?.remove() }
    }

    override suspend fun addSchedule(schedule: Schedule) {
        schedulesCollection()?.add(schedule)?.await()
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        schedulesCollection()?.document(schedule.id)?.set(schedule)?.await()
    }

    override suspend fun deleteSchedule(scheduleId: String) {
        schedulesCollection()?.document(scheduleId)?.delete()?.await()
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
