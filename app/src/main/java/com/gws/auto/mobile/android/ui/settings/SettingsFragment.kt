package com.gws.auto.mobile.android.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.SignInActivity
import com.gws.auto.mobile.android.databinding.FragmentSettingsBinding
import com.gws.auto.mobile.android.domain.service.GoogleApiAuthorizer
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var authorizer: GoogleApiAuthorizer
    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        authorizer = GoogleApiAuthorizer(requireActivity())
        auth = FirebaseAuth.getInstance()
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")
        updateUI()
        setupSpinners()
    }

    private fun setupSpinners() {
        // First day of week spinner
        val firstDayOfWeekAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.first_day_of_week_entries,
            android.R.layout.simple_spinner_item
        )
        firstDayOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.firstDayOfWeekSpinner.adapter = firstDayOfWeekAdapter
        val firstDayOfWeek = prefs.getString("first_day_of_week", "Sunday")
        val firstDayOfWeekPosition = firstDayOfWeekAdapter.getPosition(firstDayOfWeek)
        binding.firstDayOfWeekSpinner.setSelection(firstDayOfWeekPosition)
        binding.firstDayOfWeekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                prefs.edit().putString("first_day_of_week", parent.getItemAtPosition(position).toString()).apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Country spinner
        val countryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.country_entries,
            android.R.layout.simple_spinner_item
        )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.adapter = countryAdapter
        val country = prefs.getString("country", "US")
        val countryValues = resources.getStringArray(R.array.country_values)
        val countryPosition = countryValues.indexOf(country)
        binding.countrySpinner.setSelection(countryPosition)
        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                prefs.edit().putString("country", countryValues[position]).apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Language spinner
        val languageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_entries,
            android.R.layout.simple_spinner_item
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = languageAdapter
        val language = prefs.getString("language", "English")
        val languagePosition = languageAdapter.getPosition(language)
        binding.languageSpinner.setSelection(languagePosition)
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                prefs.edit().putString("language", selectedLanguage).apply()
                val locale = when (selectedLanguage) {
                    "Japanese" -> "ja"
                    "Chinese" -> "zh"
                    "Korean" -> "ko"
                    else -> "en"
                }
                val appLocale = LocaleListCompat.forLanguageTags(locale)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateUI() {
        if (auth.currentUser != null) {
            // User is signed in
            Timber.d("User is logged in. Auth button set to 'Sign Out'.")
            binding.authButton.text = getString(R.string.sign_out)
            binding.authButton.setOnClickListener {
                Timber.d("Sign out button clicked.")
                signOut()
            }
        } else {
            // User is signed out
            Timber.d("User is not logged in. Auth button set to 'Sign In'.")
            binding.authButton.text = getString(R.string.sign_in)
            binding.authButton.setOnClickListener {
                Timber.d("Sign in button clicked.")
                startActivity(Intent(activity, SignInActivity::class.java))
            }
        }
    }

    private fun signOut() {
        lifecycleScope.launch {
            authorizer.signOut()
            auth.signOut()
            Timber.i("User signed out successfully.")
            // Re-update the UI after sign out
            updateUI()
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume called")
        // Update UI in case the user signs in and returns to this fragment
        updateUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
