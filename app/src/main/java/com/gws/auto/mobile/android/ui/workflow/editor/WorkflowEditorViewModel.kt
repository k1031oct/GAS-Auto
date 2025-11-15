package com.gws.auto.mobile.android.ui.workflow.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkflowEditorViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _workflowId = MutableStateFlow<String?>(null)
    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    fun loadWorkflow(workflowId: String) {
        _workflowId.value = workflowId
        viewModelScope.launch {
            val workflow = workflowRepository.getWorkflowById(workflowId)
            workflow?.let { 
                _modules.value = it.modules
            }
        }
    }

    fun addModule(module: Module) {
        _modules.value = _modules.value + module
    }

    fun removeModule(module: Module) {
        _modules.value = _modules.value.filter { it.id != module.id }
    }

    fun updateModuleParameters(moduleId: String, parameters: Map<String, String>) {
        _modules.value = _modules.value.map {
            if (it.id == moduleId) {
                it.copy(parameters = parameters)
            } else {
                it
            }
        }
    }

    suspend fun saveNewWorkflow(name: String, description: String) {
        val workflow = Workflow(
            id = _workflowId.value ?: UUID.randomUUID().toString(),
            name = name,
            description = description,
            modules = _modules.value
        )
        workflowRepository.saveWorkflow(workflow)
    }
}
