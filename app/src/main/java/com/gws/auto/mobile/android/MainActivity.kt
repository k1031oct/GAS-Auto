package com.gws.auto.mobile.android

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.gws.auto.mobile.android.databinding.ActivityMainBinding
import com.gws.auto.mobile.android.ui.MainFragmentStateAdapter
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import com.gws.auto.mobile.android.ui.announcement.AnnouncementViewModel
import com.gws.auto.mobile.android.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainSharedViewModel: MainSharedViewModel by viewModels()
    private lateinit var announcementViewModel: AnnouncementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate called")
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        announcementViewModel = ViewModelProvider(this)[AnnouncementViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupViewPager()
        setupBottomNavigation()
        setupSearchView()
        setupSettingsIcon()
        setupBackButtonHandler()
        observeViewModel()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = MainFragmentStateAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavView.menu.getItem(position).isChecked = true
                mainSharedViewModel.setCurrentPage(position)
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_workflow -> binding.viewPager.currentItem = 0
                R.id.navigation_schedule -> binding.viewPager.currentItem = 1
                R.id.navigation_history -> binding.viewPager.currentItem = 2
                R.id.navigation_dashboard -> binding.viewPager.currentItem = 3
            }
            true
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainSharedViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupSettingsIcon() {
        binding.actionSettingsIcon.setOnClickListener {
            showSettingsMenu(it)
        }
    }

    private fun observeViewModel() {
        mainSharedViewModel.currentPage.onEach { page ->
            val hint = when(page) {
                0 -> getString(R.string.search_workflows_hint)
                1 -> "Search schedules..." // TODO: Add to strings.xml
                2 -> "Search history..."   // TODO: Add to strings.xml
                3 -> "Search dashboard..." // TODO: Add to strings.xml
                else -> ""
            }
            binding.searchView.queryHint = hint
        }.launchIn(lifecycleScope)

        announcementViewModel.hasUnread.onEach { hasUnread ->
            binding.settingsBadge.visibility = if (hasUnread) View.VISIBLE else View.GONE
        }.launchIn(lifecycleScope)
    }

    private fun setupBackButtonHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.viewPager.currentItem == 0) {
                    showExitConfirmationDialog()
                } else {
                    binding.viewPager.currentItem = 0
                }
            }
        })
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.exit_confirmation_title))
            .setMessage(getString(R.string.exit_confirmation_message))
            .setPositiveButton(getString(R.string.exit)) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showSettingsMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.settings_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            val intent = Intent(this, SettingsActivity::class.java)
            val fragmentKey = when (menuItem.itemId) {
                R.id.navigation_announcement -> "announcement"
                R.id.settings_application -> "app_settings"
                R.id.about_app -> "about_app"
                else -> null
            }
            if (fragmentKey != null) {
                if (fragmentKey == "announcement") {
                    announcementViewModel.markAllAsRead()
                }
                intent.putExtra("fragment_to_load", fragmentKey)
                startActivity(intent)
            }
            true
        }
        popup.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // We are using a custom layout in the toolbar, so we don't inflate a menu here.
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
