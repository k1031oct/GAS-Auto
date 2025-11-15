package com.gws.auto.mobile.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.gws.auto.mobile.android.ui.history.HistoryViewModel
import com.gws.auto.mobile.android.ui.search.SearchFragment
import com.gws.auto.mobile.android.ui.settings.SettingsActivity
import com.gws.auto.mobile.android.ui.workflow.WorkflowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainSharedViewModel: MainSharedViewModel by viewModels()
    private lateinit var announcementViewModel: AnnouncementViewModel
    private val workflowViewModel: WorkflowViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

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
        setupActionButtons()
        setupBackButtonHandler()
        observeViewModel()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = MainFragmentStateAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNav.menu.getItem(position).isChecked = true
                mainSharedViewModel.setCurrentPage(position)
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val pageIndex = when (item.itemId) {
                R.id.navigation_workflow -> 0
                R.id.navigation_schedule -> 1
                R.id.navigation_history -> 2
                R.id.navigation_dashboard -> 3
                else -> 0
            }
            binding.viewPager.setCurrentItem(pageIndex, true)
            true
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mainSharedViewModel.setSearchQuery(query.orEmpty())
                if (!query.isNullOrBlank()) {
                    workflowViewModel.addSearchHistory(query)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainSharedViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val searchFragment = SearchFragment()
                searchFragment.show(supportFragmentManager, searchFragment.tag)
            }
        }
    }

    private fun setupActionButtons() {
        binding.actionSettings.setOnClickListener { showSettingsMenu(it) }
        binding.actionFavorite.setOnClickListener { workflowViewModel.toggleFavoriteFilter() }
        binding.actionBookmark.setOnClickListener { historyViewModel.toggleBookmarkFilter() }
        binding.deleteAllButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete All History")
                .setMessage("Are you sure you want to delete all execution history? This action cannot be undone.")
                .setPositiveButton("Delete") { _, _ -> historyViewModel.clearHistory() }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun observeViewModel() {
        mainSharedViewModel.currentPage.onEach { pageIndex ->
            val isWorkflowPage = pageIndex == 0
            val isHistoryPage = pageIndex == 2

            binding.actionFavorite.visibility = if (isWorkflowPage) View.VISIBLE else View.GONE
            binding.actionBookmark.visibility = if (isHistoryPage) View.VISIBLE else View.GONE
            binding.deleteAllButton.visibility = if (isHistoryPage) View.VISIBLE else View.GONE
        }.launchIn(lifecycleScope)

        workflowViewModel.isFavoriteFilterActive.onEach {
            binding.actionFavorite.isChecked = it
        }.launchIn(lifecycleScope)

        historyViewModel.isBookmarkFilterActive.onEach {
            binding.actionBookmark.isChecked = it
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
}
