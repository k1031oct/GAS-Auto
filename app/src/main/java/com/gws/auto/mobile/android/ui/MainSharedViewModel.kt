package com.gws.auto.mobile.android.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainSharedViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun setSignedInStatus(isSignedIn: Boolean) {
        _isSignedIn.value = isSignedIn
    }
}
