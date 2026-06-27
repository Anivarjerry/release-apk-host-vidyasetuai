package com.vidyasetuai.feature_journey.data.local.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.vidyasetuai.MainActivity
import java.util.*

class JourneyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("alarm_type") ?: "morning"
        val title = if (type == "morning") "सुबह की विद्या जर्नी!" else "शाम की एमसीक्यू परीक्षा!"
        val message = if (type == "morning") {
            "आज के आपके नए लक्ष्य और कार्य उपलब्ध हैं। अभी जांचें!"
        } else {
            "आज की एमसीक्यू अभ्यास परीक्षा देने का समय हो गया है!"
        }

        showNotification(context, title, message)
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "vidya_journey_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vidya Journey Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily morning task and evening MCQ practice reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        fun scheduleDailyAlarms(context: Context, notificationTimeStr: String?) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // 1. Morning Alarm (8:00 AM)
            val morningIntent = Intent(context, JourneyAlarmReceiver::class.java).apply {
                putExtra("alarm_type", "morning")
            }
            val morningPendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                morningIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val calendarMorning = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendarMorning.timeInMillis,
                            morningPendingIntent
                        )
                    } else {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendarMorning.timeInMillis,
                            morningPendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendarMorning.timeInMillis,
                        morningPendingIntent
                    )
                }
            } catch (e: Exception) {
                // Fail silently or fallback
            }

            // 2. Evening Alarm (User preferred time, e.g., "19:30" or default 8:00 PM "20:00")
            var hour = 20
            var minute = 0
            if (!notificationTimeStr.isNullOrEmpty()) {
                val parts = notificationTimeStr.split(":")
                if (parts.size >= 2) {
                    hour = parts[0].toIntOrNull() ?: 20
                    minute = parts[1].substring(0, 2).toIntOrNull() ?: 0
                }
            }

            val eveningIntent = Intent(context, JourneyAlarmReceiver::class.java).apply {
                putExtra("alarm_type", "evening")
            }
            val eveningPendingIntent = PendingIntent.getBroadcast(
                context,
                1002,
                eveningIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val calendarEvening = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendarEvening.timeInMillis,
                            eveningPendingIntent
                        )
                    } else {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendarEvening.timeInMillis,
                            eveningPendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendarEvening.timeInMillis,
                        eveningPendingIntent
                    )
                }
            } catch (e: Exception) {
                // Fail silently
            }
        }
    }
}
