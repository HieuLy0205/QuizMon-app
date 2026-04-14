package com.example.quizmon.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val messages = listOf(
            "🐱 Pet đói rồi! Cho ăn đi bạn ơi~",
            "👋 Bạn có quên mình không? Vào xem nào!",
            "⏰ Còn công việc chưa hoàn thành đó nha!",
            "🌟 Hôm nay bạn đã học được gì chưa?",
            "💪 Cố lên! Hoàn thành nhiệm vụ thôi nào~",
            "🎯 Mình đang chờ bạn quay lại đây!"
        )

        sendNotification(context, messages.random())

        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hour = sharedPref.getInt("notification_hour", 8)
        val minute = sharedPref.getInt("notification_minute", 0)

        NotificationHelper(context).scheduleDaily(hour, minute)
    }

    private fun sendNotification(context: Context, message: String) {
        val channelId = "quizmon_channel"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "QuizMon Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc nhở từ QuizMon"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("QuizMon")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }
}