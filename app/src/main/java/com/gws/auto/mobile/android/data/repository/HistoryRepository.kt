package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.HistoryDao
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.ui.dashboard.StatsSummary
import com.gws.auto.mobile.android.ui.dashboard.WorkflowExecutionCount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {

    fun getAllHistory(): Flow<List<History>> {
        return historyDao.getAllHistory()
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
