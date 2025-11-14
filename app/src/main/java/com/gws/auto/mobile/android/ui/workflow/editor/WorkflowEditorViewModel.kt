package com.gws.auto.mobile.android.ui.workflow.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkflowEditorViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules

    private var currentWorkflowId: String? = null

    fun loadWorkflow(workflowId: String) {
        currentWorkflowId = workflowId
        viewModelScope.launch {
            workflowRepository.getWorkflowById(workflowId)?.let {
                _modules.value = it.modules
            }
        }
    }

    fun addModule(module: Module) {
        _modules.value = _modules.value + module
    }

    fun removeModule(module: Module) {
        _modules.value = _modules.value - module
    }

    fun moveModule(fromPosition: Int, toPosition: Int) {
        val updatedList = _modules.value.toMutableList()
        val movedModule = updatedList.removeAt(fromPosition)
        updatedList.add(toPosition, movedModule)
        _modules.value = updatedList
    }

    fun saveNewWorkflow(name: String, description: String) {
        viewModelScope.launch {
            val newWorkflow = Workflow(
                id = currentWorkflowId ?: UUID.randomUUID().toString(),
                name = name,
                description = description,
                modules = _modules.value
            )
            workflowRepository.saveWorkflow(newWorkflow)
        }
    }
}
