package com.gws.auto.mobile.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentMainSettingsBinding
import com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment
import com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainSettingsFragment : Fragment() {

    private var _binding: FragmentMainSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateUserInfo()
        setupSettingsList()
    }

    private fun populateUserInfo() {
        val user = auth.currentUser
        if (user != null) {
            binding.userName.text = user.displayName
            binding.userEmail.text = user.email
            // binding.userAvatar.load(user.photoUrl) // Coil dependency needed
        } else {
            binding.userName.text = getString(R.string.user_name_placeholder)
            binding.userEmail.text = getString(R.string.user_email_placeholder)
        }
    }

    private fun setupSettingsList() {
        val settingsItems = listOf(
            SettingsItem("Account Connections") {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, AccountConnectionsFragment())
                    .addToBackStack(null)
                    .commit()
            },
            SettingsItem("Application Settings") {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, AppSettingsFragment())
                    .addToBackStack(null)
                    .commit()
            },
            SettingsItem("Theme") {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.settings_fragment_container, ThemeSettingsFragment())
                    .addToBackStack(null)
                    .commit()
            },
            SettingsItem("About this App") {
                // Create and navigate to an AboutAppFragment
            }
        )

        binding.settingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SettingsAdapter(settingsItems)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
