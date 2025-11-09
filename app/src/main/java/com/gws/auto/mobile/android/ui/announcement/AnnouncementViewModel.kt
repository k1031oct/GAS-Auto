package com.gws.auto.mobile.android.ui.announcement

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.gws.auto.mobile.android.data.model.Announcement
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    companion object {
        private const val KEY_READ_ANNOUNCEMENTS = "read_announcement_ids"
    }

    init {
        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        val dummyData = listOf(
            Announcement("1", "アップデートのお知らせ", "バージョン1.3をリリースしました。新しい機能をお試しください。", System.currentTimeMillis(), false),
            Announcement("2", "メンテナンス情報", "明日午前2時から3時の間、サーバーメンテナンスを実施します。", System.currentTimeMillis() - 86400000, true)
        )

        val readIds = prefs.getStringSet(KEY_READ_ANNOUNCEMENTS, emptySet()) ?: emptySet()

        _announcements.value = dummyData.map { announcement ->
            if (announcement.id in readIds) {
                announcement.copy(isRead = true)
            } else {
                announcement
            }
        }
    }

    fun markAsRead(announcementId: String) {
        // Update in-memory list
        val updatedList = _announcements.value.map {
            if (it.id == announcementId) {
                it.copy(isRead = true)
            } else {
                it
            }
        }
        _announcements.value = updatedList

        // Persist the new read ID
        val readIds = prefs.getStringSet(KEY_READ_ANNOUNCEMENTS, emptySet())?.toMutableSet() ?: mutableSetOf()
        readIds.add(announcementId)
        prefs.edit {
            putStringSet(KEY_READ_ANNOUNCEMENTS, readIds)
        }
    }
}
