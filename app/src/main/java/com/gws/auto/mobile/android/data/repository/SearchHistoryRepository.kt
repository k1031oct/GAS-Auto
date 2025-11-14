package com.gws.auto.mobile.android.data.repository

import com.gws.auto.mobile.android.data.local.db.SearchHistoryDao
import com.gws.auto.mobile.android.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) {

    fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchHistoryDao.getSearchHistory()
    }

    suspend fun insertSearchHistory(query: String) {
        searchHistoryDao.insertSearchHistory(SearchHistory(query = query))
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearSearchHistory()
    }

    suspend fun deleteSearchHistory(query: String) {
        searchHistoryDao.deleteSearchHistory(query)
    }
}
