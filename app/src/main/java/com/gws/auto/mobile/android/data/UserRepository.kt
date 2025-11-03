package com.gws.auto.mobile.android.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveUserSetting(key: String, value: Any) {
        val userId = auth.currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(userId)

        userDocRef.update(key, value)
    }

    fun getUserSetting(key: String): Task<DocumentSnapshot>? {
        val userId = auth.currentUser?.uid ?: return null
        return db.collection("users").document(userId).get()
    }
}
