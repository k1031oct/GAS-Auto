package com.gws.auto.mobile.android.ui.settings.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.SignInActivity
import com.gws.auto.mobile.android.databinding.FragmentAccountConnectionsBinding
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import com.gws.auto.mobile.android.ui.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AccountConnectionsFragment : Fragment() {

    private var _binding: FragmentAccountConnectionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var authorizer: GoogleApiAuthorizer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountConnectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun updateUI() {
        val user = auth.currentUser
        if (user != null) {
            binding.syncStatusText.text = getString(R.string.sync_enabled_with, user.email)
            binding.authButton.text = getString(R.string.sign_out_and_disable_sync)
            binding.authButton.setOnClickListener {
                signOut()
            }
        } else {
            binding.syncStatusText.text = getString(R.string.sync_disabled)
            binding.authButton.text = getString(R.string.sign_in_with_google_to_sync)
            binding.authButton.setOnClickListener {
                startActivity(Intent(activity, SignInActivity::class.java))
            }
        }
    }

    private fun signOut() {
        authorizer.signOut {
            auth.signOut()
            Timber.i("User signed out successfully. Sync disabled.")
            activity?.runOnUiThread {
                updateUI()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            lifecycleScope.launch {
                viewModel.syncWorkflows()
            }
        }
        updateUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
