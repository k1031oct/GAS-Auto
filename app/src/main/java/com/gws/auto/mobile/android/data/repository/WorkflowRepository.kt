package com.gws.auto.mobile.android.data.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gws.auto.mobile.android.domain.model.Workflow
import javax.inject.Inject

class WorkflowRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun getWorkflowsCollection() = auth.currentUser?.uid?.let { uid ->
        db.collection("users")
            .document(uid)
            .collection("workflows")
    }

    fun saveWorkflow(workflow: Workflow): Task<Void> {
        val collection = getWorkflowsCollection()
        return if (collection != null) {
            collection.document(workflow.id).set(workflow)
        } else {
            Tasks.forException(IllegalStateException("User is not logged in."))
        }
    }

    fun getWorkflow(workflowId: String) = getWorkflowsCollection()?.document(workflowId)?.get()

    fun getAllWorkflows(): Task<QuerySnapshot> {
        val collection = getWorkflowsCollection()
        return collection?.get() ?: Tasks.forException(IllegalStateException("User is not logged in."))
    }

    fun deleteWorkflow(workflowId: String): Task<Void> {
        val collection = getWorkflowsCollection()
        return if (collection != null) {
            collection.document(workflowId).delete()
        } else {
            Tasks.forException(IllegalStateException("User is not logged in."))
        }
    }
}
