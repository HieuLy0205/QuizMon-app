package com.example.quizmon.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class StreakManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("streak", Context.MODE_PRIVATE)

    fun checkAndUpdateStreak() {
        val lastLoginDate = prefs.getLong("last_login_date", 0)
        val currentDate = Calendar.getInstance().time.time

        val lastLogin = Date(lastLoginDate)
        val today = Date(currentDate)

        val daysDifference = getDaysDifference(lastLogin, today)

        val currentStreak = prefs.getInt("current_streak", 0)

        val newStreak = when {
            daysDifference == 1 -> currentStreak + 1
            daysDifference > 1 -> 1
            else -> currentStreak
        }

        prefs.edit().putInt("current_streak", newStreak).apply()
        prefs.edit().putLong("last_login_date", currentDate).apply()

        // Cập nhật longest streak
        val longestStreak = prefs.getInt("longest_streak", 0)
        if (newStreak > longestStreak) {
            prefs.edit().putInt("longest_streak", newStreak).apply()
        }
    }

    private fun getDaysDifference(date1: Date, date2: Date): Int {
        val diffInMillis = date2.time - date1.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getCurrentStreak(): Int {
        return prefs.getInt("current_streak", 0)
    }

    fun getLongestStreak(): Int {
        return prefs.getInt("longest_streak", 0)
    }

    fun resetStreak() {
        prefs.edit().putInt("current_streak", 0).apply()
    }
}