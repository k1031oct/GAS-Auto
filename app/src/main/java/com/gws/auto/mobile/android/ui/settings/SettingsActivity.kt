package com.gws.auto.mobile.android.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ActivitySettingsBinding
import com.gws.auto.mobile.android.ui.settings.account.AccountConnectionsFragment
import com.gws.auto.mobile.android.ui.settings.app.AppSettingsFragment
import com.gws.auto.mobile.android.ui.settings.user.UserInfoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragmentKey = intent.getStringExtra("fragment_to_load")
        val fragment = when (fragmentKey) {
            "user_info" -> UserInfoFragment()
            "account_connections" -> AccountConnectionsFragment()
            "app_settings" -> AppSettingsFragment()
            else -> AppSettingsFragment() // Default to app settings
        }
        supportActionBar?.title = getTitleForFragment(fragmentKey)

        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_fragment_container, fragment)
            .commit()
    }

    private fun getTitleForFragment(key: String?): String {
        return when (key) {
            "user_info" -> getString(R.string.title_user_info)
            "account_connections" -> getString(R.string.title_account_connections)
            "app_settings" -> getString(R.string.title_app_settings)
            else -> getString(R.string.title_settings)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
