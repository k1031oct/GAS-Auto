package com.gws.auto.mobile.android.domain.service

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleApiAuthorizer @Inject constructor(@ApplicationContext private val context: Context) {

    @Suppress("DEPRECATION")
    fun signOut(onComplete: () -> Unit) {
        Identity.getSignInClient(context).signOut().addOnCompleteListener {
            onComplete()
        }
    }
}
