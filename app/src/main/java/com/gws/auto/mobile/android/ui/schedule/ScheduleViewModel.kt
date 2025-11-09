package com.gws.auto.mobile.android.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gws.auto.mobile.android.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    @Suppress("unused") private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val schedules = scheduleRepository.getSchedulesFlow().asLiveData()

}
