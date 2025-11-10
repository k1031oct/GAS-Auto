package com.gws.auto.mobile.android.ui.schedule

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.domain.model.Holiday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    init {
        loadHolidaysForCurrentMonth()
        observeSchedules()
    }

    private fun observeSchedules() {
        viewModelScope.launch {
            scheduleRepository.getSchedulesFlow()
                .catch { e -> Timber.e(e, "Error collecting schedules.") }
                .collect { scheduleList ->
                    _schedules.value = scheduleList
                }
        }
    }

    private fun loadHolidaysForCurrentMonth() {
        viewModelScope.launch {
            val countryCode = prefs.getString("country", "US") ?: "US"
            val year = _currentDate.value.year
            _holidays.value = scheduleRepository.getHolidays(countryCode, year)
        }
    }

    fun moveToNextMonth() {
        _currentDate.value = _currentDate.value.plusMonths(1)
        if (_currentDate.value.year != _currentDate.value.minusMonths(1).year) {
            loadHolidaysForCurrentMonth() // Year changed
        }
    }

    fun moveToPreviousMonth() {
        _currentDate.value = _currentDate.value.minusMonths(1)
        if (_currentDate.value.year != _currentDate.value.plusMonths(1).year) {
            loadHolidaysForCurrentMonth() // Year changed
        }
    }
}
