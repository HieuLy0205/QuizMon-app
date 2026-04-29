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
        return sharedPreferences.getInt("current_hearts", 5)
    }

    /**
     * Cộng thêm tim (không giới hạn trần 5 mạng)
     */
    fun addHearts(amount: Int) {
        val next = getHearts() + amount
        saveHearts(next)
        if (next >= 5) {
            saveLastHeartLossTime(0L) // Dừng hồi phục nếu đã đầy hoặc vượt mức
        }
    }

    fun useHeart() {
        val current = getHearts()
        if (current > 0) {
            val next = current - 1
            saveHearts(next)
            // Chỉ bắt đầu đếm ngược khi tim rơi xuống dưới mức 5
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
     * Tự động hồi phục tim (Mốc 5 mạng)
     */
    fun autoRegenerateHearts(): Long {
        var currentHearts = getHearts()
        if (currentHearts >= 5) {
            saveLastHeartLossTime(0L)
            return 0
        }

        val lastTime = getLastHeartLossTime()
        if (lastTime == 0L) {
            // Đảm bảo luôn có mốc thời gian nếu tim < 5
            saveLastHeartLossTime(System.currentTimeMillis())
            return 3 * 60 * 1000L
        }

        val currentTime = System.currentTimeMillis()
        val diff = currentTime - lastTime
        val recoveryInterval = 3 * 60 * 1000L // 3 phút

        val heartsToRecover = (diff / recoveryInterval).toInt()
        if (heartsToRecover > 0) {
            val nextHearts = (currentHearts + heartsToRecover).coerceAtMost(5)
            saveHearts(nextHearts)
            
            if (nextHearts < 5) {
                saveLastHeartLossTime(lastTime + (heartsToRecover * recoveryInterval))
            } else {
                saveLastHeartLossTime(0L)
            }
            currentHearts = nextHearts
        }

        return if (currentHearts < 5) {
            recoveryInterval - (diff % recoveryInterval)
        } else {
            0
        }
    }

    // --- QUẢN LÝ NHIỆM VỤ ---
    fun isTaskCompletedToday(taskId: String): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastData = sharedPreferences.getString("task_$taskId", null)
        return lastData == currentDate
    }
    fun markTaskCompletedToday(taskId: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        sharedPreferences.edit().putString("task_$taskId", currentDate).apply()
    }
    fun Dk_batmo_xn(taskId: String, isReady: Boolean) {
        sharedPreferences.edit().putBoolean("ready_$taskId", isReady).apply()
    }
    fun Dk_xacnhan_cq(taskId: String): Boolean {
        return sharedPreferences.getBoolean("ready_$taskId", false)
    }
    // --- QUẢN LÝ PET ---
    fun getPetLevel(): Int{
        //vì sao cần biến : vì khi đổi bet thì tên key có thể cộng vào id
        val currentPetid = getPetid()
        if(currentPetid == -1) return 1
        return sharedPreferences.getInt("pet_level_$currentPetid", 1)
    }
    fun savePetLevel(level: Int){
        val currentPetid = getPetid()
        if (currentPetid != -1) {
            sharedPreferences.edit().putInt("pet_level_$currentPetid", level).apply()
        }
    }
    fun getPetid(): Int {
        return sharedPreferences.getInt("pet_id", -1)
    }
    fun savePetid(id: Int) {
        sharedPreferences.edit().putInt("pet_id", id).apply()
    }
    fun addpetid(id: Int) {
        val nextid = getPetid() + id
        savePetid(nextid)
    // savePetid(getPetid() + id)
    }

    // --- QUẢN LÝ DANH SÁCH SỞ HỮU ---
    fun getOwnedPetIds(): List<String> {
        val ownedStr = sharedPreferences.getString("owned_pets", "") ?: ""
        return if (ownedStr.isEmpty()) emptyList() else ownedStr.split(",")
    }

    fun addOwnedPet(id: String) {
        val owned = getOwnedPetIds().toMutableList()
        if (!owned.contains(id)) {
            owned.add(id)
            sharedPreferences.edit().putString("owned_pets", owned.joinToString(",")).apply()
        }
    }

    fun getOwnedEggIds(): List<String> {
        val ownedStr = sharedPreferences.getString("owned_eggs", "") ?: ""
        return if (ownedStr.isEmpty()) emptyList() else ownedStr.split(",")
    }

    fun addOwnedEgg(id: String) {
        val owned = getOwnedEggIds().toMutableList()
        if (!owned.contains(id)) {
            owned.add(id)
            sharedPreferences.edit().putString("owned_eggs", owned.joinToString(",")).apply()
        }
    }
}
