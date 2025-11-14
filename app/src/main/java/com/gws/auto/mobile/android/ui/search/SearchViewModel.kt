package com.gws.auto.mobile.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.SearchHistoryRepository
import com.gws.auto.mobile.android.data.repository.TagRepository
import com.gws.auto.mobile.android.domain.model.SearchHistory
import com.gws.auto.mobile.android.domain.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    val tags: StateFlow<List<Tag>> = tagRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchHistory: StateFlow<List<SearchHistory>> = searchHistoryRepository.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTag(tagName: String) = viewModelScope.launch {
        tagRepository.addTag(Tag(name = tagName))
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        tagRepository.deleteTag(tag)
    }

    fun addSearchHistory(query: String) = viewModelScope.launch {
        searchHistoryRepository.insertSearchHistory(query)
    }

    fun clearSearchHistory() = viewModelScope.launch {
        searchHistoryRepository.clearSearchHistory()
    }

    fun deleteSearchHistoryItem(query: String) = viewModelScope.launch {
        searchHistoryRepository.deleteSearchHistory(query)
    }
}
