package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import java.time.LocalTime

data class ScheduleSettingsUiState(
    val scheduleType: String = "時間毎",
    // Hourly
    val hourlyInterval: Int = 1,
    // Daily
    val dailyTime: LocalTime = LocalTime.now(),
    // Weekly
    val weeklyDays: Set<String> = emptySet(),
    val weeklyTime: LocalTime = LocalTime.now(),
    // Monthly
    val monthlyDays: Set<Int> = emptySet(),
    val monthlyTime: LocalTime = LocalTime.now(),
)

@HiltViewModel
class ScheduleSettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleSettingsUiState())
    val uiState: StateFlow<ScheduleSettingsUiState> = _uiState.asStateFlow()

    fun onScheduleTypeChange(newType: String) {
        _uiState.update { it.copy(scheduleType = newType) }
    }

    fun setHourlyInterval(interval: Int) {
        _uiState.update { it.copy(hourlyInterval = interval) }
    }

    fun setDailyTime(time: LocalTime) {
        _uiState.update { it.copy(dailyTime = time) }
    }

    fun toggleWeeklyDay(day: String) {
        _uiState.update { currentState ->
            val newDays = currentState.weeklyDays.toMutableSet()
            if (newDays.contains(day)) {
                newDays.remove(day)
            } else {
                newDays.add(day)
            }
            currentState.copy(weeklyDays = newDays)
        }
    }

    fun setWeeklyTime(time: LocalTime) {
        _uiState.update { it.copy(weeklyTime = time) }
    }

    fun toggleMonthlyDay(day: Int) {
        _uiState.update { currentState ->
            val newDays = currentState.monthlyDays.toMutableSet()
            if (newDays.contains(day)) {
                newDays.remove(day)
            } else {
                newDays.add(day)
            }
            currentState.copy(monthlyDays = newDays)
        }
    }

    fun setMonthlyTime(time: LocalTime) {
        _uiState.update { it.copy(monthlyTime = time) }
    }


    fun saveSchedule() {
        // TODO: Implement the logic to save the schedule settings
        println("Saving schedule with state: ${_uiState.value}")
    }
}
