package com.vidyasetuai.core.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vidyasetuai.core.auth.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token generated: $token")
        
        // Save token locally
        val sessionManager = SessionManager(applicationContext)
        sessionManager.saveFcmToken(token)

        // Sync token immediately if session exists
        if (sessionManager.hasActiveSession()) {
            CoroutineScope(Dispatchers.IO).launch {
                com.vidyasetuai.core.auth.AuthManager.syncFcmToken(applicationContext, sessionManager)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Message received from: ${message.from}")

        // Delegate to central AppNotificationManager
        AppNotificationManager.handleIncomingFcmMessage(applicationContext, message)
    }
}
