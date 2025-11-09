package com.gws.auto.mobile.android.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gws.auto.mobile.android.domain.notification.NotificationHelper
import timber.log.Timber

class ScheduleWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("ScheduleWorker executing...")

        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.createNotificationChannel()

        // TODO: Get workflowId from inputData and execute the corresponding workflow
        val workflowName = inputData.getString("workflow_name") ?: "Workflow"

        // Simulate workflow execution
        val isSuccess = true // Replace with actual workflow execution result

        notificationHelper.showStatusNotification(workflowName, isSuccess)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
