package com.example.quizmon.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.example.quizmon.data.model.Statistics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class StatisticsLocalDataSource(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("stats", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveDailyStats(stats: Statistics) {
        val calendar = Calendar.getInstance()
        calendar.time = stats.date
        val dateKey = "stats_${calendar.get(Calendar.YEAR)}_${calendar.get(Calendar.MONTH)}_${calendar.get(Calendar.DAY_OF_MONTH)}"

        val json = gson.toJson(stats)
        prefs.edit().putString(dateKey, json).apply()
    }

    fun getDailyStats(date: Date): Statistics? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dateKey = "stats_${calendar.get(Calendar.YEAR)}_${calendar.get(Calendar.MONTH)}_${calendar.get(Calendar.DAY_OF_MONTH)}"

        val json = prefs.getString(dateKey, null)
        return if (json != null) gson.fromJson(json, Statistics::class.java) else null
    }

    fun getAllStats(): List<Statistics> {
        val statsList = mutableListOf<Statistics>()
        val allEntries = prefs.all

        for ((key, value) in allEntries) {
            if (key.startsWith("stats_") && value is String) {
                try {
                    val stats = gson.fromJson(value, Statistics::class.java)
                    statsList.add(stats)
                } catch (e: Exception) {
                    // Bỏ qua nếu parse lỗi
                }
            }
        }
        return statsList.sortedBy { it.date }
    }

    fun saveCurrentStreak(streak: Int) {
        prefs.edit().putInt("current_streak", streak).apply()
    }

    fun getCurrentStreak(): Int {
        return prefs.getInt("current_streak", 0)
    }

    fun saveLongestStreak(streak: Int) {
        val currentLongest = getLongestStreak()
        if (streak > currentLongest) {
            prefs.edit().putInt("longest_streak", streak).apply()
        }
    }

    fun getLongestStreak(): Int {
        return prefs.getInt("longest_streak", 0)
    }
}