package com.gws.auto.mobile.android

import android.content.Context
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLoggingTree(private val context: Context) : Timber.DebugTree() {

    private val logFile: File by lazy {
        val logDir = context.getExternalFilesDir(null) ?: context.filesDir
        File(logDir, "debug.log")
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < Log.DEBUG) {
            return // Ignore VERBOSE and other lower level logs
        }

        try {
            val logTimeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val priorityChar = when (priority) {
                Log.INFO -> "I"
                Log.WARN -> "W"
                Log.ERROR -> "E"
                Log.DEBUG -> "D"
                else -> "?"
            }

            val logText = "$logTimeStamp $priorityChar/$tag: $message\n"

            FileWriter(logFile, true).use {
                it.append(logText)
            }

            if (t != null) {
                FileWriter(logFile, true).use {
                    it.append(t.stackTraceToString())
                    it.append("\n")
                }
            }
        } catch (e: Exception) {
            Log.e("FileLoggingTree", "Error while logging into file", e)
        }
    }
}
