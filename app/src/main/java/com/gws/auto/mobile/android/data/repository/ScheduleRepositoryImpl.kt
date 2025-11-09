// Forcing a file refresh to fix import issue.
package com.gws.auto.mobile.android.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gws.auto.mobile.android.data.model.Schedule
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ScheduleRepository {

    private val userId: String?
        get() = auth.currentUser?.uid

    private fun schedulesCollection() = userId?.let { firestore.collection("users/$it/schedules") }

    override fun getSchedulesFlow(): Flow<List<Schedule>> = callbackFlow {
        val listenerRegistration = schedulesCollection()?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val schedules = snapshot?.documents?.mapNotNull { it.toObject(Schedule::class.java) } ?: emptyList()
            trySend(schedules)
        }
        awaitClose { listenerRegistration?.remove() }
    }

    override suspend fun addSchedule(schedule: Schedule) {
        schedulesCollection()?.add(schedule)?.await()
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        schedulesCollection()?.document(schedule.id)?.set(schedule)?.await()
    }

    override suspend fun deleteSchedule(scheduleId: String) {
        schedulesCollection()?.document(scheduleId)?.delete()?.await()
    }
}
