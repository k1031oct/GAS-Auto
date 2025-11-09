package com.gws.auto.mobile.android.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import timber.log.Timber

class ScheduleWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("ScheduleWorker executing...")
        // TODO: Get workflowId from inputData and execute the corresponding workflow

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
