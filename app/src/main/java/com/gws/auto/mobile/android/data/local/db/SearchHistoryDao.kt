package com.gws.auto.mobile.android.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gws.auto.mobile.android.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getSearchHistory(): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearchHistory(query: String)
}
