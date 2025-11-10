package com.gws.auto.mobile.android.ui.settings

import androidx.lifecycle.ViewModel
import com.gws.auto.mobile.android.data.repository.WorkflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    suspend fun syncWorkflows() {
        workflowRepository.syncWorkflows()
    }
}
