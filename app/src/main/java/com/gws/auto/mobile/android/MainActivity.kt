package com.gws.auto.mobile.android

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
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.gws.auto.mobile.android.databinding.ActivityMainBinding
import com.gws.auto.mobile.android.ui.MainFragmentStateAdapter
import com.gws.auto.mobile.android.ui.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate called")
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Hide default title

        setupViewPager()
        setupBottomNavigation()
        setupSearchView()
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
            // Navigation to settings screens needs a new implementation
            when (menuItem.itemId) {
                R.id.navigation_announcement -> Timber.d("Navigate to Announcement")
                R.id.settings_user_info -> Timber.d("Navigate to User Info")
                R.id.settings_account_connections -> Timber.d("Navigate to Account Connections")
                R.id.settings_application -> Timber.d("Navigate to Application Settings")
            }
            true
        }
        popup.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Since the menu item is now part of the Toolbar, we need a reliable anchor.
                val anchorView = findViewById<View>(R.id.action_settings)
                if (anchorView != null) {
                    showSettingsMenu(anchorView)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
