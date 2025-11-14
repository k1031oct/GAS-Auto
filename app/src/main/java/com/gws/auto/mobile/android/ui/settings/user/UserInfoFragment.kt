package com.gws.auto.mobile.android.ui.settings.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentUserInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UserInfoFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!

    @Inject
    @JvmField
    var auth: FirebaseAuth? = null

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Timber.w(e, "Google sign in failed")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setOnClickListener { signIn() }
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {
                    Timber.d("Sign-in with Google successful.")
                    updateUI()
                } else {
                    Timber.w(it.exception, "Sign-in with Google failed.")
                }
            }
    }

    private fun updateUI() {
        val user = auth?.currentUser
        if (user != null) {
            binding.userName.text = user.displayName
            binding.userEmail.text = user.email
            binding.profileImage.load(user.photoUrl) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher_round)
                error(R.mipmap.ic_launcher_round)
            }
            binding.signInButton.visibility = View.GONE
        } else {
            binding.userName.text = getString(R.string.user_name_placeholder)
            binding.userEmail.text = getString(R.string.user_email_placeholder)
            binding.profileImage.setImageResource(R.mipmap.ic_launcher_round)
            binding.signInButton.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
