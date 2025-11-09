package com.gws.auto.mobile.android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.gws.auto.mobile.android.databinding.ActivitySignInBinding
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authorizer: GoogleApiAuthorizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        authorizer = GoogleApiAuthorizer(this)

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        lifecycleScope.launch {
            try {
                val result = authorizer.signIn(getString(R.string.default_web_client_id))
                val googleIdTokenCredential = authorizer.getGoogleIdTokenCredential(result)
                val idToken = googleIdTokenCredential.idToken
                firebaseAuthWithGoogle(idToken)
            } catch (e: GetCredentialException) {
                Timber.e(e, "Error during sign-in")
                Toast.makeText(this@SignInActivity, "Sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Firebase authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }
}
