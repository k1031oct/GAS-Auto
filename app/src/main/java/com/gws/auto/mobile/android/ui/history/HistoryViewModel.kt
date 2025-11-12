package com.gws.auto.mobile.android.ui.history

import androidx.lifecycle.ViewModel
import com.gws.auto.mobile.android.BuildConfig
import com.gws.auto.mobile.android.domain.model.WorkflowExecutionLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HistoryViewModel @Inject constructor() : ViewModel() {

    private val _executionLogs = MutableStateFlow<List<WorkflowExecutionLog>>(emptyList())
    val executionLogs: StateFlow<List<WorkflowExecutionLog>> = _executionLogs.asStateFlow()

    init {
        loadExecutionLogs()
    }

    private fun loadExecutionLogs() {
        if (BuildConfig.DEBUG) {
            _executionLogs.value = createDummyLogs()
        } else {
            // TODO: Load real logs from a repository
        }
    }

    private fun createDummyLogs(): List<WorkflowExecutionLog> {
        val names = listOf("Generate Report", "Send Daily Summary", "Archive Files")
        val statuses = listOf("Success", "Success", "Failure", "Success")
        return List(20) {
            WorkflowExecutionLog(
                id = UUID.randomUUID().toString(),
                workflowName = names.random(),
                executionTime = Date(System.currentTimeMillis() - Random.nextLong(1000 * 60 * 60 * 24 * 7)), // within last week
                status = statuses.random(),
                durationMs = Random.nextLong(100, 5000)
            )
        }.sortedByDescending { it.executionTime }
    }
}
