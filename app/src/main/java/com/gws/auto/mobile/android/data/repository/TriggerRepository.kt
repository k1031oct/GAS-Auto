package com.gws.auto.mobile.android.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gws.auto.mobile.android.domain.model.Trigger

class TriggerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getTriggersCollection() = db.collection("users")
        .document(auth.currentUser!!.uid)
        .collection("triggers")

    fun saveTrigger(trigger: Trigger): Task<Void> {
        return getTriggersCollection().document(trigger.id).set(trigger)
    }

    fun getTrigger(triggerId: String) = getTriggersCollection().document(triggerId).get()

    fun getAllTriggers(): Task<QuerySnapshot> {
        return getTriggersCollection().get()
    }

    fun deleteTrigger(triggerId: String): Task<Void> {
        return getTriggersCollection().document(triggerId).delete()
    }
}
