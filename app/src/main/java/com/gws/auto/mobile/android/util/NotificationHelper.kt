package com.gws.auto.mobile.android.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.gws.auto.mobile.android.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID = "announcement_channel"
        private const val CHANNEL_NAME = "Announcements"
        private const val NOTIFICATION_ID = 1
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    fun showUnreadAnnouncementNotification() {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_announcement)
        builder.setContentTitle(context.getString(R.string.title_announcement))
        builder.setContentText(context.getString(R.string.new_announcement_notification))
        builder.priority = NotificationCompat.PRIORITY_DEFAULT

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
