package com.gws.auto.mobile.android.domain.service

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleApiAuthorizer(private val activity: Activity) {

    private val credentialManager by lazy { CredentialManager.create(activity) }

    suspend fun signIn(serverClientId: String): GetCredentialResponse {
        val googleIdOption = GetGoogleIdOption.builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.builder()
            .addCredentialOption(googleIdOption)
            .build()

        return credentialManager.getCredential(activity, request)
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    fun getGoogleIdTokenCredential(response: GetCredentialResponse): GoogleIdTokenCredential {
        return GoogleIdTokenCredential.createFrom(response.credential.data)
    }
}
