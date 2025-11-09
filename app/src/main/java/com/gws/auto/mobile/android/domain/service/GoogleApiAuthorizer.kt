package com.gws.auto.mobile.android.domain.service

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.ClearCredentialStateRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class GoogleApiAuthorizer @Inject constructor(@ActivityContext private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(serverClientId: String): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun getGoogleIdTokenCredential(request: GetCredentialRequest): GoogleIdTokenCredential {
        val result: GetCredentialResponse = credentialManager.getCredential(context, request)
        return GoogleIdTokenCredential.createFrom(result.credential.data)
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}
