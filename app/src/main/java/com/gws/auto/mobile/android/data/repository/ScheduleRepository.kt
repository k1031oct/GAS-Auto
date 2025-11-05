package com.gws.auto.mobile.android.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gws.auto.mobile.android.domain.model.Schedule

class ScheduleRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getSchedulesCollection() = db.collection("users")
        .document(auth.currentUser!!.uid)
        .collection("schedules")

    fun saveSchedule(schedule: Schedule): Task<Void> {
        return getSchedulesCollection().document(schedule.id).set(schedule)
    }

    fun getSchedule(scheduleId: String) = getSchedulesCollection().document(scheduleId).get()

    fun getAllSchedules(): Task<QuerySnapshot> {
        return getSchedulesCollection().get()
    }

    fun deleteSchedule(scheduleId: String): Task<Void> {
        return getSchedulesCollection().document(scheduleId).delete()
    }
}
