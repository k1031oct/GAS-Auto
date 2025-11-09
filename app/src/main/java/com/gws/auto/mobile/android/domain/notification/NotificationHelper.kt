package com.gws.auto.mobile.android.domain.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.gws.auto.mobile.android.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID = "workflow_status_channel"
        private const val CHANNEL_NAME = "Workflow Status"
        private const val CHANNEL_DESCRIPTION = "Notifications for workflow execution status"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showStatusNotification(workflowName: String, isSuccess: Boolean) {
        val title = if (isSuccess) "Workflow Succeeded" else "Workflow Failed"
        val message = if (isSuccess) {
            "'$workflowName' has completed successfully."
        } else {
            "'$workflowName' failed to complete."
        }
        val notificationId = System.currentTimeMillis().toInt()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // You need to add this icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
