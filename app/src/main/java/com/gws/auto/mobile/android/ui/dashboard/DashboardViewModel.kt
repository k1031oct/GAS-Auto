package com.gws.auto.mobile.android.ui.dashboard

import androidx.lifecycle.ViewModel
import com.gws.auto.mobile.android.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

data class DashboardStat(val title: String, val value: String)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _stats = MutableStateFlow<List<DashboardStat>>(emptyList())
    val stats: StateFlow<List<DashboardStat>> = _stats.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        if (BuildConfig.DEBUG) {
            _stats.value = createDummyStats()
        } else {
            // TODO: Load real stats from a repository
        }
    }

    private fun createDummyStats(): List<DashboardStat> {
        return listOf(
            DashboardStat("Total Executions", Random.nextInt(100, 1000).toString()),
            DashboardStat("Successful Executions", Random.nextInt(100, 900).toString()),
            DashboardStat("Failed Executions", Random.nextInt(5, 50).toString()),
            DashboardStat("Time Saved (Hours)", "≈${Random.nextInt(10, 100)}")
        )
    }
}
