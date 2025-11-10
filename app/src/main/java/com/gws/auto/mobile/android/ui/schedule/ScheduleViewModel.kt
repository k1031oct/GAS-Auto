package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.gws.auto.mobile.android.data.repository.CalendarRepository
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    scheduleRepository: Lazy<ScheduleRepository>,
    private val calendarRepository: Lazy<CalendarRepository>,
    private val googleApiAuthorizer: GoogleApiAuthorizer
) : ViewModel() {

    val schedules = scheduleRepository.get().getSchedulesFlow().asLiveData()

    private val _calendarEvents = MutableStateFlow<List<Event>>(emptyList())
    val calendarEvents: StateFlow<List<Event>> = _calendarEvents

    fun fetchCalendarEvents(calendar: Calendar) {
        val accountEmail = googleApiAuthorizer.getCurrentUser()?.email

        if (accountEmail.isNullOrEmpty()) {
            // Not signed in or no email available, clear the events
            _calendarEvents.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val startTime = Calendar.getInstance().apply {
                    timeInMillis = calendar.timeInMillis
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
                val endTime = Calendar.getInstance().apply {
                    timeInMillis = calendar.timeInMillis
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }

                val startDateTime = DateTime(startTime.time, TimeZone.getDefault())
                val endDateTime = DateTime(endTime.time, TimeZone.getDefault())

                val events = calendarRepository.get().getEvents(accountEmail, startDateTime, endDateTime)
                _calendarEvents.value = events ?: emptyList()
            } catch (e: GoogleAuthIOException) {
                Timber.e(e, "Authentication error when fetching calendar events.")
                _calendarEvents.value = emptyList() // Clear events on auth error
            } catch (e: IOException) {
                Timber.e(e, "Network error when fetching calendar events.")
                _calendarEvents.value = emptyList() // Clear events on network error
            } catch (e: Exception) {
                Timber.e(e, "An unexpected error occurred while fetching calendar events.")
                _calendarEvents.value = emptyList() // Clear events on other errors
            }
        }
    }
}
