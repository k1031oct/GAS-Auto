package com.gws.auto.mobile.android.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import com.gws.auto.mobile.android.domain.model.Workflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkflowRepository @Inject constructor(
    private val workflowDao: WorkflowDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun getWorkflowsCollection() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("workflows")
    }

    // Always fetch from the local database as the single source of truth.
    fun getAllWorkflows(): Flow<List<Workflow>> {
        return workflowDao.getAllWorkflows()
    }

    // Save to local DB, then try to sync with Firestore if logged in.
    suspend fun saveWorkflow(workflow: Workflow) {
        workflowDao.insertWorkflow(workflow)
        if (auth.currentUser != null) {
            try {
                getWorkflowsCollection()?.document(workflow.id)?.set(workflow)?.await()
                Timber.d("Workflow saved to Firestore.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to save workflow to Firestore.")
                // Optionally, implement a mechanism to retry failed syncs.
            }
        }
    }

    // Delete from local DB, then try to sync with Firestore if logged in.
    suspend fun deleteWorkflow(workflow: Workflow) {
        workflowDao.deleteWorkflow(workflow)
        if (auth.currentUser != null) {
            try {
                getWorkflowsCollection()?.document(workflow.id)?.delete()?.await()
                Timber.d("Workflow deleted from Firestore.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete workflow from Firestore.")
            }
        }
    }

    // Syncs workflows from Firestore to the local database.
    suspend fun syncWorkflows() {
        if (auth.currentUser == null) return

        try {
            val snapshot = getWorkflowsCollection()?.get()?.await()
            snapshot?.documents?.forEach { document ->
                val workflow = document.toObject(Workflow::class.java)
                if (workflow != null) {
                    workflowDao.insertWorkflow(workflow)
                }
            }
            Timber.d("Workflows synced from Firestore.")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync workflows from Firestore.")
        }
    }
}
