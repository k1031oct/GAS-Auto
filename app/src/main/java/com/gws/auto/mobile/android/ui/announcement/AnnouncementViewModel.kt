package com.gws.auto.mobile.android.ui.announcement

import androidx.lifecycle.ViewModel
import com.gws.auto.mobile.android.data.model.Announcement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor() : ViewModel() {

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        _announcements.value = listOf(
            Announcement("1", "アップデートのお知らせ", "バージョン1.3をリリースしました。新しい機能をお試しください。", System.currentTimeMillis(), false),
            Announcement("2", "メンテナンス情報", "明日午前2時から3時の間、サーバーメンテナンスを実施します。", System.currentTimeMillis() - 86400000, true)
        )
    }

    fun markAsRead(announcementId: String) {
        val updatedList = _announcements.value.map {
            if (it.id == announcementId) {
                it.copy(isRead = true)
            } else {
                it
            }
        }
        _announcements.value = updatedList
    }
}
