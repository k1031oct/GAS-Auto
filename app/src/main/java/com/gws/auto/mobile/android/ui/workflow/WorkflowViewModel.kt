package com.gws.auto.mobile.android.ui.workflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _isFavoriteFilterActive = MutableStateFlow(false)
    val isFavoriteFilterActive: StateFlow<Boolean> = _isFavoriteFilterActive

    val filteredWorkflows: StateFlow<List<Workflow>> = combine(
        workflowRepository.getAllWorkflows(),
        _searchQuery,
        _isFavoriteFilterActive
    ) { workflows, query, isFavoriteFilterActive ->
        val filtered = if (query.isBlank()) {
            workflows
        } else {
            workflows.filter { it.name.contains(query, ignoreCase = true) }
        }
        if (isFavoriteFilterActive) {
            filtered.filter { it.isFavorite }
        } else {
            filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteWorkflow(workflow: Workflow) = viewModelScope.launch {
        workflowRepository.deleteWorkflow(workflow)
    }

    fun toggleFavorite(workflow: Workflow) = viewModelScope.launch {
        val updatedWorkflow = workflow.copy(isFavorite = !workflow.isFavorite)
        workflowRepository.saveWorkflow(updatedWorkflow)
    }

    fun toggleFavoriteFilter() {
        _isFavoriteFilterActive.value = !_isFavoriteFilterActive.value
    }
}
