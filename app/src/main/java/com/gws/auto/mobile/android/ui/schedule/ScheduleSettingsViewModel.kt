package com.gws.auto.mobile.android.ui.schedule

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import com.gws.auto.mobile.android.domain.worker.ScheduleWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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
    // Yearly
    val yearlyMonth: Int = ZonedDateTime.now().monthValue,
    val yearlyDayOfMonth: Int = ZonedDateTime.now().dayOfMonth,
    val yearlyTime: LocalTime = LocalTime.now(),
)

@HiltViewModel
class ScheduleSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

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

    fun setYearlyMonth(month: Int) {
        _uiState.update { it.copy(yearlyMonth = month) }
    }

    fun setYearlyDayOfMonth(day: Int) {
        _uiState.update { it.copy(yearlyDayOfMonth = day) }
    }

    fun setYearlyTime(time: LocalTime) {
        _uiState.update { it.copy(yearlyTime = time) }
    }


    fun saveSchedule() {
        val uiState = _uiState.value
        val schedule = Schedule(
            // workflowId will be set later
            scheduleType = uiState.scheduleType,
            hourlyInterval = if (uiState.scheduleType == "時間毎") uiState.hourlyInterval else null,
            time = when (uiState.scheduleType) {
                "日毎" -> uiState.dailyTime.toString()
                "週毎" -> uiState.weeklyTime.toString()
                "月毎" -> uiState.monthlyTime.toString()
                "年毎" -> uiState.yearlyTime.toString()
                else -> null
            },
            weeklyDays = if (uiState.scheduleType == "週毎") uiState.weeklyDays.toList() else null,
            monthlyDays = if (uiState.scheduleType == "月毎") uiState.monthlyDays.toList() else null,
            yearlyMonth = if (uiState.scheduleType == "年毎") uiState.yearlyMonth else null,
            yearlyDayOfMonth = if (uiState.scheduleType == "年毎") uiState.yearlyDayOfMonth else null,
            isEnabled = true
        )

        viewModelScope.launch {
            scheduleRepository.addSchedule(schedule)
            scheduleWork(schedule)
        }
    }

    private fun scheduleWork(schedule: Schedule) {
        val workManager = WorkManager.getInstance(context)

        val workRequest = when (schedule.scheduleType) {
            "時間毎" -> {
                PeriodicWorkRequestBuilder<ScheduleWorker>(
                    schedule.hourlyInterval!!.toLong(),
                    TimeUnit.HOURS
                ).build()
            }
            "日毎" -> {
                val now = ZonedDateTime.now()
                var nextRun = now.with(LocalTime.parse(schedule.time))
                if (nextRun.isBefore(now)) {
                    nextRun = nextRun.plusDays(1)
                }
                val delay = Duration.between(now, nextRun).toMillis()
                OneTimeWorkRequestBuilder<ScheduleWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()
            }
            "週毎" -> {
                val now = ZonedDateTime.now()
                val targetTime = LocalTime.parse(schedule.time)
                val targetDays = schedule.weeklyDays!!.map { dayString ->
                    when (dayString) {
                        "月" -> DayOfWeek.MONDAY
                        "火" -> DayOfWeek.TUESDAY
                        "水" -> DayOfWeek.WEDNESDAY
                        "木" -> DayOfWeek.THURSDAY
                        "金" -> DayOfWeek.FRIDAY
                        "土" -> DayOfWeek.SATURDAY
                        "日" -> DayOfWeek.SUNDAY
                        else -> throw IllegalArgumentException("Invalid day of week")
                    }
                }.toSet()

                var nextRun = now.with(targetTime)
                while (!targetDays.contains(nextRun.dayOfWeek) || nextRun.isBefore(now)) {
                    nextRun = nextRun.plusDays(1)
                }

                val delay = Duration.between(now, nextRun).toMillis()
                OneTimeWorkRequestBuilder<ScheduleWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()
            }
            "月毎" -> {
                val now = ZonedDateTime.now()
                val targetTime = LocalTime.parse(schedule.time)
                val targetDays = schedule.monthlyDays!!.toSet()

                var nextRun = now.with(targetTime)
                while (!targetDays.contains(nextRun.dayOfMonth) || nextRun.isBefore(now)) {
                    nextRun = nextRun.plusDays(1)
                }
                val delay = Duration.between(now, nextRun).toMillis()
                OneTimeWorkRequestBuilder<ScheduleWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()
            }
            "年毎" -> {
                val now = ZonedDateTime.now()
                val targetTime = LocalTime.parse(schedule.time)
                var nextRun = now
                    .withMonth(schedule.yearlyMonth!!)
                    .withDayOfMonth(schedule.yearlyDayOfMonth!!)
                    .with(targetTime)

                if (nextRun.isBefore(now)) {
                    nextRun = nextRun.plusYears(1)
                }
                val delay = Duration.between(now, nextRun).toMillis()
                OneTimeWorkRequestBuilder<ScheduleWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()
            }
            else -> null
        }

        if (workRequest != null) {
            workManager.enqueue(workRequest)
        }
    }
}
