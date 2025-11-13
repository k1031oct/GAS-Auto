package com.gws.auto.mobile.android.data.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gws.auto.mobile.android.data.local.db.WorkflowDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth?,
    private val workflowDao: WorkflowDao
) {
    // Firestore synchronization logic will be implemented here.
}
