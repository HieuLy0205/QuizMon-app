package com.example.quizmon.ui.shop

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)

    // --- QUẢN LÝ SAO (Stars) ---
    fun saveCoins(coins: Int) {
        sharedPreferences.edit().putInt("current_coins", coins).apply()
    }
    fun getCoins(): Int {
        return sharedPreferences.getInt("current_coins", 0)
    }
    fun addCoin(amount: Int) {
        saveCoins(getCoins() + amount)
    }

    // --- QUẢN LÝ XU (Coins) ---
    fun saveXu(xu: Int) {
        sharedPreferences.edit().putInt("current_xu", xu).apply()
    }
    fun getXu(): Int {
        return sharedPreferences.getInt("current_xu", 0)
    }
    fun addXu(amount: Int) {
        saveXu(getXu() + amount)
    }

    // --- QUẢN LÝ KINH NGHIỆM (Exp) ---
    fun saveExp(exp: Int) {
        sharedPreferences.edit().putInt("current_exp", exp).apply()
    }
    fun getExp(): Int {
        return sharedPreferences.getInt("current_exp", 0)
    }
    fun addExp(amount: Int) {
        saveExp(getExp() + amount)
    }

    /**
     * Lấy chuỗi câu trả lời đúng liên tiếp hiện tại
     */
    fun getCorrectStreak(): Int {
        return sharedPreferences.getInt("correct_streak", 0)
    }

    /**
     * Xử lý khi trả lời đúng:
     * - Tăng chuỗi câu đúng
     * - Tính EXP: câu đầu +10, câu 2 +11, câu 3 +12...
     * - Trả về số EXP vừa cộng để hiển thị UI nếu cần
     */
    fun handleCorrectAnswer(): Int {
        val newStreak = getCorrectStreak() + 1
        sharedPreferences.edit().putInt("correct_streak", newStreak).apply()

        val expToAdd = 10 + (newStreak - 1)
        addExp(expToAdd)
        return expToAdd
    }

    /**
     * Xử lý khi trả lời sai: Reset chuỗi câu đúng về 0
     */
    fun handleWrongAnswer() {
        sharedPreferences.edit().putInt("correct_streak", 0).apply()
    }

    // --- QUẢN LÝ TIM (Hearts/Life) ---
    fun saveHearts(hearts: Int) {
        sharedPreferences.edit().putInt("current_hearts", hearts).apply()
    }
    fun getHearts(): Int {
        return sharedPreferences.getInt("current_hearts", 5) // Mặc định 5 tim
    }
    fun useHeart() {
        val current = getHearts()
        if (current > 0) {
            saveHearts(current - 1)
            // Nếu dùng tim đầu tiên (đang từ 5 xuống 4), bắt đầu mốc thời gian hồi tim
            if (current == 5) {
                saveLastHeartLossTime(System.currentTimeMillis())
            }
        }
    }

    fun saveLastHeartLossTime(time: Long) {
        sharedPreferences.edit().putLong("last_heart_loss_time", time).apply()
    }

    fun getLastHeartLossTime(): Long {
        return sharedPreferences.getLong("last_heart_loss_time", 0L)
    }

    /**
     * Tự động hồi phục tim dựa trên thời gian thực
     * Trả về thời gian còn lại (ms) của lượt hồi hiện tại
     */
    fun autoRegenerateHearts(): Long {
        var currentHearts = getHearts()
        if (currentHearts >= 5) return 0

        val lastTime = getLastHeartLossTime()
        if (lastTime == 0L) return 0

        val currentTime = System.currentTimeMillis()
        val diff = currentTime - lastTime
        val recoveryTime = 3 * 60 * 1000L // 3 phút = 180,000ms

        val heartsToRecover = (diff / recoveryTime).toInt()
        if (heartsToRecover > 0) {
            val newHearts = (currentHearts + heartsToRecover).coerceAtMost(5)
            saveHearts(newHearts)

            if (newHearts < 5) {
                // Tiếp tục đếm ngược từ mốc mới
                saveLastHeartLossTime(lastTime + (heartsToRecover * recoveryTime))
            } else {
                saveLastHeartLossTime(0L)
            }
            currentHearts = newHearts
        }

        return if (currentHearts < 5) {
            recoveryTime - (diff % recoveryTime)
        } else {
            0
        }
    }

    // --- QUẢN LÝ NHIỆM VỤ ---
    fun isTaskCompletedToday(taskId: String): Boolean {
        val lastData = sharedPreferences.getString("task_$taskId", null)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return lastData == currentDate
    }
    fun markTaskCompletedToday(taskId: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        sharedPreferences.edit().putString("task_$taskId", currentDate).apply()
    }
    fun Dk_Ainho_Addcoin(taskId: String, isReady: Boolean) {
        sharedPreferences.edit().putBoolean("task_$taskId", isReady).apply()
    }
    fun Dk_Ainho_coin(taskId: String): Boolean {
        return sharedPreferences.getBoolean("task_$taskId", false)
    }

    // --- QUẢN LÝ PET ---
    fun getPetLevel(): Int{
        return sharedPreferences.getInt("pet_level", 1)
    }
    fun savePetLevel(level: Int){
        sharedPreferences.edit().putInt("pet_level", level).apply()
    }
    fun getPetid(): Int {
        return sharedPreferences.getInt("pet_id", 1)
    }
    fun savePetid(id: Int) {
        sharedPreferences.edit().putInt("pet_id", id).apply()
    }
}
