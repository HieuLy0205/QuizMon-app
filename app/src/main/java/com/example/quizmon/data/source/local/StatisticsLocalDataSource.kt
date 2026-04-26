package com.example.quizmon.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.example.quizmon.data.model.Statistics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

/**
 * Lớp quản lý lưu trữ dữ liệu thống kê vào SharedPreferences.
 * Đã đồng bộ hoàn toàn với StatisticsRepository.
 */
class StatisticsLocalDataSource(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("quizmon_stats_pref", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    companion object {
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_LONGEST_STREAK = "longest_streak"
        private const val KEY_START_DATE = "start_date"
        private const val KEY_LEVELS_COMPLETED = "levels_completed"
        private const val STATS_PREFIX = "stats_"
    }

    /**
     * Lưu thống kê của một ngày cụ thể
     */
    fun saveDailyStats(stats: Statistics) {
        val calendar = Calendar.getInstance()
        calendar.time = stats.date
        val dateKey = "$STATS_PREFIX${calendar.get(Calendar.YEAR)}_${calendar.get(Calendar.MONTH)}_${calendar.get(Calendar.DAY_OF_MONTH)}"

        val json = gson.toJson(stats)
        prefs.edit().putString(dateKey, json).apply()
    }

    /**
     * Lấy thống kê của một ngày cụ thể
     */
    fun getDailyStats(date: Date): Statistics? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dateKey = "$STATS_PREFIX${calendar.get(Calendar.YEAR)}_${calendar.get(Calendar.MONTH)}_${calendar.get(Calendar.DAY_OF_MONTH)}"

        val json = prefs.getString(dateKey, null) ?: return null
        return try {
            gson.fromJson(json, Statistics::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Lấy toàn bộ lịch sử thống kê
     */
    fun getAllStats(): List<Statistics> {
        val statsList = mutableListOf<Statistics>()
        val allEntries = prefs.all

        for ((key, value) in allEntries) {
            if (key.startsWith(STATS_PREFIX) && value is String) {
                try {
                    val stats = gson.fromJson(value, Statistics::class.java)
                    if (stats != null) statsList.add(stats)
                } catch (e: Exception) {
                    // Bỏ qua bản ghi lỗi
                }
            }
        }
        return statsList.sortedBy { it.date }
    }

    // --- Quản lý Streak (Chuỗi ngày) ---

    fun saveCurrentStreak(streak: Int) {
        prefs.edit().putInt(KEY_CURRENT_STREAK, streak).apply()
    }

    fun getCurrentStreak(): Int = prefs.getInt(KEY_CURRENT_STREAK, 0)

    fun saveLongestStreak(streak: Int) {
        val currentLongest = getLongestStreak()
        if (streak > currentLongest) {
            prefs.edit().putInt(KEY_LONGEST_STREAK, streak).apply()
        }
    }

    fun getLongestStreak(): Int = prefs.getInt(KEY_LONGEST_STREAK, 0)

    // --- Quản lý Cấp độ và Ngày bắt đầu ---

    fun getStartDate(): Long {
        var firstTime = prefs.getLong(KEY_START_DATE, 0L)
        if (firstTime == 0L) {
            firstTime = System.currentTimeMillis()
            prefs.edit().putLong(KEY_START_DATE, firstTime).apply()
        }
        return firstTime
    }

    fun getLevelsCompleted(): Int = prefs.getInt(KEY_LEVELS_COMPLETED, 0)

    fun incrementLevelsCompleted() {
        val current = getLevelsCompleted()
        prefs.edit().putInt(KEY_LEVELS_COMPLETED, current + 1).apply()
    }
}
