package com.gws.auto.mobile.android.ui.announcement

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.gws.auto.mobile.android.data.model.Announcement
import com.gws.auto.mobile.android.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val notificationHelper = NotificationHelper(context)

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    private val _hasUnread = MutableStateFlow(false)
    val hasUnread: StateFlow<Boolean> = _hasUnread

    companion object {
        private const val KEY_READ_ANNOUNCEMENTS = "read_announcement_ids"
    }

    init {
        notificationHelper.createNotificationChannel()
        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        val dummyData = listOf(
            Announcement("1", "アップデートのお知らせ", "バージョン1.3をリリースしました。新しい機能をお試しください。", System.currentTimeMillis(), false),
            Announcement("2", "メンテナンス情報", "明日午前2時から3時の間、サーバーメンテナンスを実施します。", System.currentTimeMillis() - 86400000, true)
        )

        val readIds = prefs.getStringSet(KEY_READ_ANNOUNCEMENTS, emptySet()) ?: emptySet()

        val loadedAnnouncements = dummyData.map { announcement ->
            if (announcement.id in readIds) {
                announcement.copy(isRead = true)
            } else {
                announcement
            }
        }
        _announcements.value = loadedAnnouncements

        val hasUnreadNow = loadedAnnouncements.any { !it.isRead }
        if (hasUnreadNow) {
            _hasUnread.value = true
            notificationHelper.showUnreadAnnouncementNotification()
        }
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

        val readIds = prefs.getStringSet(KEY_READ_ANNOUNCEMENTS, emptySet())?.toMutableSet() ?: mutableSetOf()
        readIds.add(announcementId)
        prefs.edit { putStringSet(KEY_READ_ANNOUNCEMENTS, readIds) }

        if (updatedList.none { !it.isRead }) {
            _hasUnread.value = false
        }
    }
    
    fun markAllAsRead() {
        val allIds = _announcements.value.map { it.id }.toSet()
        prefs.edit { putStringSet(KEY_READ_ANNOUNCEMENTS, allIds) }
        _announcements.value = _announcements.value.map { it.copy(isRead = true) }
        _hasUnread.value = false
    }
}
