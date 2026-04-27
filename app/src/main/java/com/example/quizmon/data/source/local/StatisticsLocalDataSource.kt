package com.example.quizmon.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.example.quizmon.data.model.Statistics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

class StatisticsLocalDataSource(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("quizmon_stats_pref", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    companion object {
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_LONGEST_STREAK = "longest_streak"
        private const val KEY_START_DATE = "start_date"
        private const val KEY_LEVELS_COMPLETED = "levels_completed"
        
        // Keys cho Thành tích
        private const val KEY_MATCHES_WON = "matches_won"
        private const val KEY_TOTAL_QUESTIONS = "total_questions_answered"
        private const val KEY_PETS_COLLECTED = "pets_collected"
        private const val KEY_HARD_LEVELS = "hard_levels_completed"
        
        private const val STATS_PREFIX = "stats_"
    }

    fun saveDailyStats(stats: Statistics) {
        val calendar = Calendar.getInstance()
        calendar.time = stats.date
        val dateKey = "$STATS_PREFIX${calendar.get(Calendar.YEAR)}_${calendar.get(Calendar.MONTH)}_${calendar.get(Calendar.DAY_OF_MONTH)}"
        prefs.edit().putString(dateKey, gson.toJson(stats)).apply()
    }

    fun getDailyStats(date: Date): Statistics? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dateKey = "$STATS_PREFIX${calendar.get(Calendar.YEAR)}_${calendar.get(Calendar.MONTH)}_${calendar.get(Calendar.DAY_OF_MONTH)}"
        val json = prefs.getString(dateKey, null) ?: return null
        return try { gson.fromJson(json, Statistics::class.java) } catch (e: Exception) { null }
    }

    fun getAllStats(): List<Statistics> {
        val statsList = mutableListOf<Statistics>()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(STATS_PREFIX) && value is String) {
                try { gson.fromJson(value, Statistics::class.java)?.let { statsList.add(it) } } catch (e: Exception) {}
            }
        }
        return statsList.sortedBy { it.date }
    }

    // --- Logic cho Thành tích (Achievements) ---

    fun getMatchesWon(): Int = prefs.getInt(KEY_MATCHES_WON, 0)
    fun incrementMatchesWon() = prefs.edit().putInt(KEY_MATCHES_WON, getMatchesWon() + 1).apply()

    fun getTotalQuestionsAnswered(): Int = prefs.getInt(KEY_TOTAL_QUESTIONS, 0)
    fun addQuestionsAnswered(count: Int) = prefs.edit().putInt(KEY_TOTAL_QUESTIONS, getTotalQuestionsAnswered() + count).apply()

    fun getPetsCollected(): Int = prefs.getInt(KEY_PETS_COLLECTED, 0)
    fun updatePetsCollected(count: Int) = prefs.edit().putInt(KEY_PETS_COLLECTED, count).apply()

    fun getHardLevelsCompleted(): Int = prefs.getInt(KEY_HARD_LEVELS, 0)
    fun incrementHardLevels() = prefs.edit().putInt(KEY_HARD_LEVELS, getHardLevelsCompleted() + 1).apply()

    // --- Cấu hình chung ---
    fun getCurrentStreak(): Int = prefs.getInt(KEY_CURRENT_STREAK, 0)
    fun saveCurrentStreak(s: Int) = prefs.edit().putInt(KEY_CURRENT_STREAK, s).apply()
    fun getLongestStreak(): Int = prefs.getInt(KEY_LONGEST_STREAK, 0)
    fun saveLongestStreak(s: Int) = prefs.edit().putInt(KEY_LONGEST_STREAK, s).apply()
    fun getLevelsCompleted(): Int = prefs.getInt(KEY_LEVELS_COMPLETED, 0)
    fun incrementLevelsCompleted() = prefs.edit().putInt(KEY_LEVELS_COMPLETED, getLevelsCompleted() + 1).apply()
    fun getStartDate(): Long {
        var start = prefs.getLong(KEY_START_DATE, 0L)
        if (start == 0L) { start = System.currentTimeMillis(); prefs.edit().putLong(KEY_START_DATE, start).apply() }
        return start
    }
}