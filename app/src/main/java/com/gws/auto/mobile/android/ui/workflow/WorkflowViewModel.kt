package com.gws.auto.mobile.android.ui.workflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _workflows = MutableStateFlow<List<Workflow>>(emptyList())
    private val _query = MutableStateFlow("")
    @Suppress("unused")
    val query = _query.asStateFlow()

    val filteredWorkflows: StateFlow<List<Workflow>> =
        _query.combine(_workflows) { query, workflows ->
            if (query.isBlank()) {
                workflows
            } else {
                workflows.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    fun loadWorkflows() {
        workflowRepository.getAllWorkflows().addOnSuccessListener { documents ->
            val workflowList = mutableListOf<Workflow>()
            for (document in documents) {
                try {
                    // ドキュメントを一つずつ安全に変換
                    val workflow = document.toObject(Workflow::class.java)
                    workflowList.add(workflow)
                } catch (e: Exception) {
                    // 変換に失敗したドキュメントのIDとエラーをログに出力
                    Timber.e(e, "Failed to convert document: ${document.id}")
                }
            }
            _workflows.value = workflowList
        }.addOnFailureListener { exception ->
            Timber.e(exception, "Failed to fetch workflows")
        }
    }
}
