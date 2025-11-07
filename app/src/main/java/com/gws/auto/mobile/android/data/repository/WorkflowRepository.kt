package com.gws.auto.mobile.android.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gws.auto.mobile.android.domain.model.Workflow
import javax.inject.Inject

class WorkflowRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun getWorkflowsCollection() = db.collection("users")
        .document(auth.currentUser!!.uid)
        .collection("workflows")

    fun saveWorkflow(workflow: Workflow): Task<Void> {
        return getWorkflowsCollection().document(workflow.id).set(workflow)
    }

    fun getWorkflow(workflowId: String) = getWorkflowsCollection().document(workflowId).get()

    fun getAllWorkflows(): Task<QuerySnapshot> {
        return getWorkflowsCollection().get()
    }

    fun deleteWorkflow(workflowId: String): Task<Void> {
        return getWorkflowsCollection().document(workflowId).delete()
    }
}
