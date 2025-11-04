package com.gws.auto.mobile.android.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.SignInActivity
import com.gws.auto.mobile.android.databinding.FragmentSettingsBinding
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var authorizer: GoogleApiAuthorizer
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        authorizer = GoogleApiAuthorizer(requireActivity())
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI() {
        if (auth.currentUser != null) {
            // User is signed in
            binding.authButton.text = getString(R.string.sign_out)
            binding.authButton.setOnClickListener {
                signOut()
            }
        } else {
            // User is signed out
            binding.authButton.text = getString(R.string.sign_in)
            binding.authButton.setOnClickListener {
                startActivity(Intent(activity, SignInActivity::class.java))
            }
        }
    }

    private fun signOut() {
        lifecycleScope.launch {
            authorizer.signOut()
            auth.signOut()
            // Re-update the UI after sign out
            updateUI()
        }
    }

    override fun onResume() {
        super.onResume()
        // Update UI in case the user signs in and returns to this fragment
        updateUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
