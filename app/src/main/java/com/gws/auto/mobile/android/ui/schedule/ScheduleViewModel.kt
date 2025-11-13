package com.gws.auto.mobile.android.ui.schedule

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.BuildConfig
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val prefs: SharedPreferences
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
        loadHolidaysForCurrentMonth()
        observeSchedules()
    }

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    private fun observeSchedules() {
        viewModelScope.launch {
            scheduleRepository.getSchedulesFlow()
                .catch { e -> Timber.e(e, "Error collecting schedules.") }
                .collect { scheduleList ->
                    val combinedList = if (BuildConfig.DEBUG) {
                        scheduleList + createDummySchedules()
                    } else {
                        scheduleList
                    }
                    _schedules.value = combinedList
                }
        }
    }

    private fun createDummySchedules(): List<Schedule> {
        return listOf(
            Schedule(id = UUID.randomUUID().toString(), workflowId = "Dummy Event 1", scheduleType = "daily", time = "10:00"),
            Schedule(id = UUID.randomUUID().toString(), workflowId = "Dummy Event 2", scheduleType = "monthly", monthlyDays = listOf(15), time = "14:30"),
            Schedule(id = UUID.randomUUID().toString(), workflowId = "Long Name Workflow To Test Ellipsis", scheduleType = "monthly", monthlyDays = listOf(15), time = "15:00")
        )
    }

    fun loadHolidaysForCurrentMonth() {
        viewModelScope.launch {
            val countryCode = prefs.getString("country", "US") ?: "US"
            val year = _currentDate.value.year
            _holidays.value = scheduleRepository.getHolidays(countryCode, year)
        }
    }

    fun moveToNextMonth() {
        val nextMonth = _currentDate.value.plusMonths(1)
        if (_currentDate.value.year != nextMonth.year) {
            loadHolidaysForCurrentMonth()
        }
        _currentDate.value = nextMonth
    }

    fun moveToPreviousMonth() {
        val prevMonth = _currentDate.value.minusMonths(1)
        if (_currentDate.value.year != prevMonth.year) {
            loadHolidaysForCurrentMonth()
        }
        _currentDate.value = prevMonth
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "country") {
            loadHolidaysForCurrentMonth()
        }
    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }
}
