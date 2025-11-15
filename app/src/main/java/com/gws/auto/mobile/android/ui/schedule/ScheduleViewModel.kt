package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate

    val schedules: StateFlow<List<Schedule>> = scheduleRepository.getSchedulesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays

    val firstDayOfWeek = settingsRepository.firstDayOfWeek
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sunday")

    init {
        loadHolidaysForCurrentMonth()
        settingsRepository.country
            .onEach { loadHolidaysForCurrentMonth() }
            .launchIn(viewModelScope)
    }

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
        loadHolidaysForCurrentMonth()
    }

    fun moveToNextMonth() {
        _currentDate.value = _currentDate.value.plusMonths(1)
        loadHolidaysForCurrentMonth()
    }

    fun moveToPreviousMonth() {
        _currentDate.value = _currentDate.value.minusMonths(1)
        loadHolidaysForCurrentMonth()
    }

    fun loadHolidaysForCurrentMonth() {
        viewModelScope.launch {
            val yearMonth = YearMonth.from(_currentDate.value)
            val country = settingsRepository.country.first()
            _holidays.value = scheduleRepository.getHolidays(country, yearMonth.year)
        }
    }
}
