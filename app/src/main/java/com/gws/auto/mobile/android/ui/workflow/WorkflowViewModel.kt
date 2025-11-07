package com.gws.auto.mobile.android.ui.workflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import com.gws.auto.mobile.android.domain.model.Workflow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _workflows = MutableLiveData<List<Workflow>>()
    val workflows: LiveData<List<Workflow>> = _workflows

    fun loadWorkflows() {
        workflowRepository.getAllWorkflows().addOnSuccessListener { documents ->
            _workflows.value = documents.toObjects(Workflow::class.java)
        }.addOnFailureListener {
            // Handle error
        }
    }
}
