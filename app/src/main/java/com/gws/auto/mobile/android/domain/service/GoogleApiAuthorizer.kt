package com.gws.auto.mobile.android.domain.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleApiAuthorizer @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        // Define the scopes you need here. Add more as required.
        val calendarScope = Scope("https://www.googleapis.com/auth/calendar.readonly")
        val driveScope = Scope("https://www.googleapis.com/auth/drive.file")
        val sheetsScope = Scope("https://www.googleapis.com/auth/spreadsheets")
    }

    fun getSignInIntent(serverClientId: String) =
        getGoogleSignInClient(serverClientId).signInIntent

    fun signOut(serverClientId: String, onComplete: () -> Unit) {
        getGoogleSignInClient(serverClientId).signOut().addOnCompleteListener {
            onComplete()
        }
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    private fun getGoogleSignInClient(serverClientId: String) =
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .requestScopes(calendarScope, driveScope, sheetsScope)
                .build()
        )
}
