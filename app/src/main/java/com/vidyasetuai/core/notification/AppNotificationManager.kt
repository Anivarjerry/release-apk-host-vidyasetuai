package com.vidyasetuai.core.notification

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.vidyasetuai.core.notification.handler.ChatNotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppNotificationManager {

    fun init(context: Context) {
        NotificationChannels.createAllNotificationChannels(context)
    }

    fun handleIncomingFcmMessage(context: Context, message: RemoteMessage) {
        Log.d("AppNotificationManager", "FCM payload received: ${message.data}")

        val type = message.data["type"] ?: "GENERAL"
        when (type) {
            "CHAT_MESSAGE" -> {
                val senderName = message.data["sender_name"] ?: message.notification?.title ?: "New Message"
                val rawBody = message.data["body"] ?: message.notification?.body ?: "Sent you a message"
                val roomId = message.data["room_id"] ?: ""
                val senderId = message.data["sender_id"] ?: ""

                // Format notification snippet: If payload is encrypted key string, show clean fallback
                val displaySnippet = if (rawBody.startsWith("ENC:") || rawBody.startsWith("gAAAAA")) {
                    "🔒 New Message"
                } else if (rawBody.startsWith("http://") || rawBody.startsWith("https://")) {
                    "📷 Photo"
                } else {
                    rawBody
                }

                if (roomId.isNotEmpty()) {
                    ChatNotificationHandler.showChatMessageNotification(
                        context = context,
                        senderName = senderName,
                        messageSnippet = displaySnippet,
                        roomId = roomId,
                        senderUserId = senderId
                    )
                }
            }
            else -> {
                val title = message.notification?.title ?: message.data["title"] ?: "VidyaSetu Alert"
                val body = message.notification?.body ?: message.data["body"] ?: "New notification"
                // Default fallback
                Log.d("AppNotificationManager", "General notification: $title - $body")
            }
        }
    }
}
