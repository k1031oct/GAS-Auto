package com.gws.auto.mobile.android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.gws.auto.mobile.android.databinding.ActivityMainBinding
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val announcementViewModel: AnnouncementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate called")
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        analytics = FirebaseAnalytics.getInstance(this)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Define top-level destinations to prevent the back arrow from appearing
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_workflow,
                R.id.navigation_schedule,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)

        checkForUnreadAnnouncements()
    }

    private fun checkForUnreadAnnouncements() {
        lifecycleScope.launch {
            val announcements = announcementViewModel.announcements.first()
            val hasUnread = announcements.any { !it.isRead }
            if (hasUnread) {
                showUnreadAnnouncementPopup()
            }
        }
    }

    private fun showUnreadAnnouncementPopup() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.unread_announcement_popup_title))
            .setMessage(getString(R.string.unread_announcement_popup_message))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_announcement)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_announcement -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_announcement)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
