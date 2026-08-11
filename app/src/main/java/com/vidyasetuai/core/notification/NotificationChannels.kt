package com.vidyasetuai.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val CHANNEL_CHAT_MESSAGES = "vidyasetu_chat_messages"
    const val CHANNEL_CONNECTION_REQUESTS = "vidyasetu_connection_requests"
    const val CHANNEL_GENERAL_ALERTS = "vidyasetu_general_alerts"

    fun createAllNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. High Priority Private Chat Messages Channel (Ringtone + Popup Banner)
        val chatChannel = NotificationChannel(
            CHANNEL_CHAT_MESSAGES,
            "Private Chat Messages",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for 1-on-1 private chat messages"
            enableVibration(true)
            setShowBadge(true)
        }

        // 2. Connection Requests Channel
        val requestsChannel = NotificationChannel(
            CHANNEL_CONNECTION_REQUESTS,
            "Inspire Connection Requests",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for new inspire requests"
            enableVibration(true)
            setShowBadge(true)
        }

        // 3. General Alerts & System Announcements Channel
        val alertsChannel = NotificationChannel(
            CHANNEL_GENERAL_ALERTS,
            "System Alerts & Announcements",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "General updates and system announcements"
        }

        notificationManager.createNotificationChannels(listOf(chatChannel, requestsChannel, alertsChannel))
    }
}
