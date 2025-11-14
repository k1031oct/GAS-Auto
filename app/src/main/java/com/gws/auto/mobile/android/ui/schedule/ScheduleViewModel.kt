package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.data.repository.UserPreferencesRepository
import com.gws.auto.mobile.android.domain.model.Holiday
import com.gws.auto.mobile.android.domain.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate = _currentDate.asStateFlow()

    val schedules: StateFlow<List<Schedule>> = scheduleRepository.getSchedulesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays = _holidays.asStateFlow()

    val firstDayOfWeek: StateFlow<String> = userPreferencesRepository.firstDayOfWeek
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "Sunday")

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun moveToNextMonth() {
        _currentDate.value = _currentDate.value.plusMonths(1).withDayOfMonth(1)
    }

    fun moveToPreviousMonth() {
        _currentDate.value = _currentDate.value.minusMonths(1).withDayOfMonth(1)
    }

    fun loadHolidaysForCurrentMonth() {
        viewModelScope.launch {
            val country = Locale.getDefault().country
            val year = YearMonth.from(_currentDate.value).year
            _holidays.value = scheduleRepository.getHolidays(country, year)
        }
    }
}
