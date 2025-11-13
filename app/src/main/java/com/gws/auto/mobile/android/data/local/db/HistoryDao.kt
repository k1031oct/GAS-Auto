package com.gws.auto.mobile.android.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gws.auto.mobile.android.domain.model.History
import com.gws.auto.mobile.android.ui.dashboard.StatsSummary
import com.gws.auto.mobile.android.ui.dashboard.WorkflowExecutionCount
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM execution_history ORDER BY executedAt DESC")
    fun getAllHistory(): Flow<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: History)

    @Query("DELETE FROM execution_history WHERE id = :historyId")
    suspend fun deleteHistoryById(historyId: Long)

    @Query("DELETE FROM execution_history")
    suspend fun deleteAllHistory()

    @Query("SELECT COUNT(*) FROM execution_history")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM execution_history WHERE status = 'Failure'")
    fun getErrorCount(): Flow<Int>

    @Query("SELECT workflowName, COUNT(*) as executionCount FROM execution_history GROUP BY workflowName ORDER BY executionCount DESC LIMIT 10")
    fun getWorkflowExecutionCounts(): Flow<List<WorkflowExecutionCount>>

    @Query("SELECT COUNT(*) as total_count, COUNT(CASE WHEN status = 'Failure' THEN 1 END) as error_count, SUM(durationMs) as total_duration FROM execution_history WHERE executedAt BETWEEN :startTime AND :endTime")
    fun getStatsForPeriod(startTime: Long, endTime: Long): Flow<StatsSummary>
}
