package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.HistoryDao
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.ui.dashboard.StatsSummary
import com.gws.auto.mobile.android.ui.dashboard.WorkflowExecutionCount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val historyDao: HistoryDao) {

    fun getAllHistory(): Flow<List<History>> {
        return historyDao.getAllHistory()
    }

    suspend fun insertHistory(history: History) {
        historyDao.insertHistory(history)
    }

    suspend fun deleteHistoryById(historyId: Long) {
        historyDao.deleteHistoryById(historyId)
    }

    suspend fun deleteAllHistory() {
        historyDao.deleteAllHistory()
    }

    fun getTotalCount(): Flow<Int> {
        return historyDao.getTotalCount()
    }

    fun getErrorCount(): Flow<Int> {
        return historyDao.getErrorCount()
    }

    fun getWorkflowExecutionCounts(): Flow<List<WorkflowExecutionCount>> {
        return historyDao.getWorkflowExecutionCounts()
    }

    fun getStatsForPeriod(startTime: Long, endTime: Long): Flow<StatsSummary> {
        return historyDao.getStatsForPeriod(startTime, endTime)
    }
}
