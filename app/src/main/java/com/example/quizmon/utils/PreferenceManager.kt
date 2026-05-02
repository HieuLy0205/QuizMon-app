package com.example.quizmon.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.*
import java.util.regex.Pattern

class PreferenceManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
    private val mapPrefs: SharedPreferences = context.getSharedPreferences("QuizMonMapPrefs", Context.MODE_PRIVATE)

    companion object {
        const val SUPPORT_5050 = "support_5050"
        const val SUPPORT_DOUBLE_CHANCE = "support_double_chance"
        const val SUPPORT_CORRECT_ANSWER = "support_correct_answer"
        const val SUPPORT_DOUBLE_POINTS = "support_double_points"
    }

    // --- TIỀN TỆ & EXP ---
    fun getCoins() = prefs.getInt("current_coins", 0)
    fun saveCoins(amount: Int) = prefs.edit { putInt("current_coins", amount) }
    fun addCoin(amount: Int) = prefs.edit { putInt("current_coins", getCoins() + amount) }

    fun getXu() = prefs.getInt("current_xu", 0)
    fun addXu(amount: Int) = prefs.edit { putInt("current_xu", getXu() + amount) }

    fun getExp() = prefs.getInt("current_exp", 0)
    fun addExp(amount: Int) = prefs.edit { putInt("current_exp", getExp() + amount) }

    // --- ĐIỂM ẢI (Stage Score) ---
    fun getLevelScore(levelId: Int) = mapPrefs.getInt("SCORE_$levelId", 0)
    
    // Sử dụng commit = true để đảm bảo dữ liệu được ghi ngay lập tức (quan trọng cho vòng quay)
    fun saveLevelScore(levelId: Int, score: Int) = mapPrefs.edit(commit = true) { 
        putInt("SCORE_$levelId", score) 
    }
    
    fun addLevelScore(levelId: Int, amount: Int) {
        val current = getLevelScore(levelId)
        saveLevelScore(levelId, current + amount)
    }

    // --- HÀM XỬ LÝ PHẦN THƯỞNG TỔNG HỢP ---
    fun applyRewardByString(reward: String, levelId: Int = -1) {
        val text = reward.lowercase()
        val matcher = Pattern.compile("\\d+").matcher(reward)
        val amount = if (matcher.find()) matcher.group().toInt() else 0
        val isNegative = text.contains("trừ") || text.contains("mất") || text.contains("lời nguyền")

        when {
            text.contains("50/50") -> addSupport(SUPPORT_5050, 1)
            text.contains("đáp án đúng") -> addSupport(SUPPORT_CORRECT_ANSWER, 1)
            text.contains("nhân đôi điểm") -> addSupport(SUPPORT_DOUBLE_POINTS, 1)
            text.contains("nhân đôi cơ hội") -> addSupport(SUPPORT_DOUBLE_CHANCE, 1)
            
            text.contains("điểm") && levelId != -1 -> {
                addLevelScore(levelId, if (isNegative) -amount else amount)
            }
            
            text.contains("xu") -> addXu(amount)
            text.contains("exp") -> addExp(amount)
            text.contains("mạng") || text.contains("tim") -> {
                if (isNegative) useHeart() else addHearts(if (amount == 0) 1 else amount)
            }
        }
    }

    // --- QUẢN LÝ TIM ---
    fun getHearts() = prefs.getInt("current_hearts", 5)
    fun addHearts(amount: Int) {
        val next = getHearts() + amount
        prefs.edit { putInt("current_hearts", next) }
        if (next >= 5) prefs.edit { putLong("last_heart_loss_time", 0L) }
    }
    fun useHeart() {
        val current = getHearts()
        if (current > 0) {
            prefs.edit { putInt("current_hearts", current - 1) }
            if (current == 5) prefs.edit { putLong("last_heart_loss_time", System.currentTimeMillis()) }
        }
    }

    fun autoRegenerateHearts(): Long {
        val current = getHearts()
        if (current >= 5) return 0
        val lastTime = prefs.getLong("last_heart_loss_time", 0L)
        if (lastTime == 0L) return 0
        
        val diff = System.currentTimeMillis() - lastTime
        val interval = 180000L // 3 mins
        val recovered = (diff / interval).toInt()
        
        if (recovered > 0) {
            val next = (current + recovered).coerceAtMost(5)
            prefs.edit { 
                putInt("current_hearts", next)
                putLong("last_heart_loss_time", if (next < 5) lastTime + (recovered * interval) else 0L)
            }
        }
        return if (getHearts() < 5) interval - (System.currentTimeMillis() - prefs.getLong("last_heart_loss_time", 0L)) % interval else 0
    }

    fun getSupportQuantity(type: String) = prefs.getInt(type, 1)
    fun addSupport(type: String, amount: Int) = prefs.edit { putInt(type, getSupportQuantity(type) + amount) }
    
    // --- PET & NHIỆM VỤ ---
    fun getPetid() = prefs.getInt("pet_id", 1)
    fun savePetid(id: Int) = prefs.edit { putInt("pet_id", id) }
    fun addpetid(id: Int) {
        val owned = getOwnedPetIds().toMutableSet()
        owned.add(id.toString())
        prefs.edit { putStringSet("owned_pet_ids", owned) }
    }
    
    fun getPetLevel() = prefs.getInt("pet_level", 1)
    fun savePetLevel(level: Int) = prefs.edit { putInt("pet_level", level) }

    fun getOwnedPetIds(): List<String> = prefs.getStringSet("owned_pet_ids", setOf("1"))?.toList() ?: listOf("1")

    // --- EGG ---
    fun getOwnedEggIds(): List<String> = prefs.getStringSet("owned_egg_ids", emptySet())?.toList() ?: emptyList()
    fun addEggId(id: Int) {
        val owned = getOwnedEggIds().toMutableSet()
        owned.add(id.toString())
        prefs.edit { putStringSet("owned_egg_ids", owned) }
    }

    fun Dk_batmo_xn(taskId: String, isReady: Boolean) = prefs.edit { putBoolean("ready_$taskId", isReady) }
    fun Dk_xacnhan_cq(taskId: String) = prefs.getBoolean("ready_$taskId", false)

    // --- DAILY TASKS ---
    fun isTaskCompletedToday(taskId: String): Boolean {
        val lastCompleted = prefs.getString("task_completed_date_$taskId", "")
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return lastCompleted == today
    }

    fun markTaskCompletedToday(taskId: String) {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit { putString("task_completed_date_$taskId", today) }
    }

    fun handleCorrectAnswer() {
        addExp(10)
        val streakManager = StreakManager(context)
        streakManager.checkAndUpdateStreak()
    }

    fun handleWrongAnswer() {
        val streakManager = StreakManager(context)
        streakManager.resetStreak()
    }
}
