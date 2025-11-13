package com.gws.auto.mobile.android.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.domain.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val executionLogs: StateFlow<List<History>> = historyRepository.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun deleteHistory(history: History) {
        viewModelScope.launch {
            historyRepository.deleteHistoryById(history.id)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            historyRepository.deleteAllHistory()
        }
    }
}
