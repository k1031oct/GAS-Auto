package com.gws.auto.mobile.android.domain.service

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.api.services.calendar.CalendarScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class Scope(val scopeUri: String) {
    object CalendarReadOnly : Scope(CalendarScopes.CALENDAR_READONLY)
}

@Singleton
class GoogleApiAuthorizer @Inject constructor(@ApplicationContext private val context: Context) {

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    @Suppress("DEPRECATION")
    fun signOut(onComplete: () -> Unit) {
        Identity.getSignInClient(context).signOut().addOnCompleteListener {
            onComplete()
        }
    }
}
