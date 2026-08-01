package com.streamdrop.app.core.util

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dual sink logger for download crash diagnosis:
 * - Logcat tag [TAG] / caller tag
 * - Append-only file: filesDir/download_debug.log
 */
object DownloadDebugLog {
    const val TAG = "StreamDropDL"
    private const val FILE_NAME = "download_debug.log"
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun i(context: Context?, tag: String, message: String) {
        Log.i(TAG, "[$tag] $message")
        persist(context, "I", tag, message, null)
    }

    fun e(context: Context?, tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, "[$tag] $message", throwable)
        } else {
            Log.e(TAG, "[$tag] $message")
        }
        persist(context, "E", tag, message, throwable)
    }

    fun clear(context: Context) {
        try {
            File(context.filesDir, FILE_NAME).writeText("")
        } catch (_: Exception) {
            // ignore
        }
    }

    fun file(context: Context): File = File(context.filesDir, FILE_NAME)

    private fun persist(
        context: Context?,
        level: String,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        if (context == null) return
        try {
            val stamp = timeFormat.format(Date())
            val builder = StringBuilder()
            builder.append(stamp)
                .append(' ')
                .append(level)
                .append(' ')
                .append(tag)
                .append(' ')
                .append(message)
                .append('\n')
            if (throwable != null) {
                builder.append(throwable::class.java.name)
                    .append(": ")
                    .append(throwable.message)
                    .append('\n')
                builder.append(Log.getStackTraceString(throwable))
                builder.append('\n')
            }
            File(context.filesDir, FILE_NAME).appendText(builder.toString())
        } catch (_: Exception) {
            // Never let logging crash the app
        }
    }
}
