package com.gws.auto.mobile.android.ui.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gws.auto.mobile.android.BuildConfig
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.FragmentAboutAppBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutAppFragment : Fragment() {

    private var _binding: FragmentAboutAppBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        displayVersion()
    }

    private fun setupClickListeners() {
        binding.manualButton.setOnClickListener {
            openUrl("https://github.com/k1031oct/GWS-Auto-for-Android") // Placeholder URL
        }
        binding.ratingButton.setOnClickListener {
            openUrl("market://details?id=${requireContext().packageName}")
        }
        binding.shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, null))
        }
        binding.privacyPolicyButton.setOnClickListener {
            openUrl("https://github.com/k1031oct/GWS-Auto-for-Android") // Placeholder URL
        }
        binding.licensesButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, LicensesFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.contactButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:k1031.oct@gmail.com") // Placeholder email
            }
            startActivity(intent)
        }
    }

    private fun displayVersion() {
        binding.versionText.text = BuildConfig.VERSION_NAME
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
