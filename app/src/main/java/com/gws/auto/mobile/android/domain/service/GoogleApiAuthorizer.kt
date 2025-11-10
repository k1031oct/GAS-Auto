package com.gws.auto.mobile.android.domain.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.gws.auto.mobile.android.R

// This is now a plain Kotlin class, without any Hilt annotations.
class GoogleApiAuthorizer(private val context: Context) {

    private fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getCalendarClient(): Calendar? {
        val account = getLastSignedInAccount() ?: return null

        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(Scope.CalendarReadOnly.scopeUri)
        ).apply {
            selectedAccount = account.account
        }

        return Calendar.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(context.getString(R.string.app_name)).build()
    }

    fun signOut(onComplete: () -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val signInClient = GoogleSignIn.getClient(context, gso)
        signInClient.signOut().addOnCompleteListener { onComplete() }
    }
}
