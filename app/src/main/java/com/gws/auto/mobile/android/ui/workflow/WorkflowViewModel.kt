package com.gws.auto.mobile.android.ui.workflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    private val _workflows = workflowRepository.getAllWorkflows()
        .catch { e ->
            Timber.e(e, "Error collecting workflows from local database.")
            emit(emptyList()) // On error, emit an empty list.
        }

    val filteredWorkflows: StateFlow<List<Workflow>> =
        query.combine(_workflows) { query, workflows ->
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

    fun addModule(module: Module) {
        _modules.value = _modules.value + module
    }

    fun removeModule(module: Module) {
        _modules.value = _modules.value - module
    }

    fun moveModule(from: Int, to: Int) {
        val updatedList = _modules.value.toMutableList()
        val movedItem = updatedList.removeAt(from)
        updatedList.add(to, movedItem)
        _modules.value = updatedList
    }

    fun saveNewWorkflow(name: String, description: String) {
        viewModelScope.launch {
            val newWorkflow = Workflow(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                modules = _modules.value,
                status = "active"
            )
            workflowRepository.saveWorkflow(newWorkflow)
            Timber.d("New workflow created: $name")
        }
    }

    fun deleteWorkflow(workflow: Workflow) {
        viewModelScope.launch {
            workflowRepository.deleteWorkflow(workflow)
            Timber.d("Workflow deleted: ${workflow.name}")
        }
    }
}
