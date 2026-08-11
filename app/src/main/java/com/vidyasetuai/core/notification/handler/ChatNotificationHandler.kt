package com.vidyasetuai.core.notification.handler

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.vidyasetuai.MainActivity
import com.vidyasetuai.R
import com.vidyasetuai.core.notification.NotificationChannels

object ChatNotificationHandler : Application.ActivityLifecycleCallbacks {

    @Volatile
    var activeChatRoomId: String? = null

    @Volatile
    var isAppInForeground: Boolean = false

    fun initLifecycle(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun showChatMessageNotification(
        context: Context,
        senderName: String,
        messageSnippet: String,
        roomId: String,
        senderUserId: String
    ) {
        // Only suppress notification sound if app is actively in foreground AND user is inside that specific chat room
        if (isAppInForeground && activeChatRoomId == roomId) return

        NotificationChannels.createAllNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NAVIGATE_TO", "private_chat_room")
            putExtra("TARGET_ROOM_ID", roomId)
            putExtra("TARGET_PEER_USER_ID", senderUserId)
        }

        val notifId = Math.abs(roomId.hashCode())

        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_CHAT_MESSAGES)
            .setSmallIcon(R.drawable.ic_bridge_logo)
            .setContentTitle(senderName)
            .setContentText(messageSnippet)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notifId, notification)
    }

    fun clearNotificationForRoom(context: Context, roomId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(roomId.hashCode())
    }

    override fun onActivityResumed(activity: Activity) {
        isAppInForeground = true
    }

    override fun onActivityPaused(activity: Activity) {
        isAppInForeground = false
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
