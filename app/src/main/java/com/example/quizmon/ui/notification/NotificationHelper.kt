package com.example.quizmon.ui.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class NotificationHelper(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleDaily(hour: Int, minute: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            val now = Calendar.getInstance()
            if (timeInMillis <= now.timeInMillis) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelNotification() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun isNotificationEnabled(): Boolean {
        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("notification_enabled", true)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("notification_enabled", enabled).apply()

        if (enabled) {
            val hour = sharedPref.getInt("notification_hour", 8)
            val minute = sharedPref.getInt("notification_minute", 0)
            scheduleDaily(hour, minute)
        } else {
            cancelNotification()
        }
    }

    fun saveNotificationTime(hour: Int, minute: Int) {
        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putInt("notification_hour", hour)
            .putInt("notification_minute", minute)
            .apply()
    }

    fun getNotificationHour(): Int {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getInt("notification_hour", 8)
    }

    fun getNotificationMinute(): Int {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getInt("notification_minute", 0)
    }
}