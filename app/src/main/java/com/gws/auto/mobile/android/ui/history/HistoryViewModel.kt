package com.gws.auto.mobile.android.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.HistoryRepository
import com.gws.auto.mobile.android.domain.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _expandedIds = MutableStateFlow<Set<Long>>(emptySet())

    val uiState: StateFlow<List<HistoryListItem>> = historyRepository.getAllHistory()
        .combine(_expandedIds) { histories, expandedIds ->
            val flatList = mutableListOf<HistoryListItem>()
            histories.forEach {
                val isExpanded = expandedIds.contains(it.id)
                flatList.add(HistoryListItem.HeaderItem(it, isExpanded))
                if (isExpanded) {
                    val logs = it.logs.lines().map { log -> HistoryListItem.LogItem(log, it.id.toString()) }
                    flatList.addAll(logs)
                }
            }
            flatList
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun toggleItemExpanded(historyId: Long) {
        val currentIds = _expandedIds.value.toMutableSet()
        if (currentIds.contains(historyId)) {
            currentIds.remove(historyId)
        } else {
            currentIds.add(historyId)
        }
        _expandedIds.value = currentIds
    }

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
