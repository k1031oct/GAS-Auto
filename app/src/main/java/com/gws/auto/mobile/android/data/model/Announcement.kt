package com.gws.auto.mobile.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)
