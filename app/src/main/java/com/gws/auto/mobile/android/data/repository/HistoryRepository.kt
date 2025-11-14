package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.HistoryDao
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.ui.dashboard.StatsSummary
import com.gws.auto.mobile.android.ui.dashboard.WorkflowExecutionCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {

    fun getAllHistory(): Flow<List<History>> {
        val dummyData = listOf(
            History(
                id = 1,
                workflowId = "wf-1",
                workflowName = "Daily Report",
                executedAt = Date(),
                status = "Success",
                logs = "Execution started...\nStep 1 completed.\nStep 2 completed.\nExecution finished.",
                durationMs = 1200
            ),
            History(
                id = 2,
                workflowId = "wf-2",
                workflowName = "File Cleanup",
                executedAt = Date(System.currentTimeMillis() - 86400000), // 1 day ago
                status = "Failure",
                logs = "Execution started...\nFailed at Step 2: File not found.",
                durationMs = 500,
                isBookmarked = true
            )
        )
        return combine(historyDao.getAllHistory(), flowOf(dummyData)) { dbHistory, dummy ->
            (dbHistory + dummy).distinctBy { it.id }
        }
    }

    suspend fun insertHistory(history: History) {
        historyDao.insertHistory(history)
    }

    suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun deleteAllHistory() {
        historyDao.deleteAllHistory()
    }

    suspend fun updateHistory(history: History) {
        historyDao.insertHistory(history) // Using insert with OnConflictStrategy.REPLACE as an update
    }

    fun getWorkflowExecutionCounts(): Flow<List<WorkflowExecutionCount>> {
        return historyDao.getWorkflowExecutionCounts()
    }

    fun getStatsForPeriod(startTime: Long, endTime: Long): Flow<StatsSummary> {
        return historyDao.getStatsForPeriod(startTime, endTime)
    }
}
