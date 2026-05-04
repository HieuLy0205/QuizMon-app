package com.example.quizmon.utils
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.quizmon.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class PreferenceManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
    private val mapPrefs: SharedPreferences = context.getSharedPreferences("QuizMonMapPrefs", Context.MODE_PRIVATE)

    companion object {
        const val SUPPORT_5050 = "support_5050"
        const val SUPPORT_DOUBLE_CHANCE = "support_double_chance"
        const val SUPPORT_CORRECT_ANSWER = "support_correct_answer"
        const val SUPPORT_DOUBLE_POINTS = "support_double_points"
    }

    // --- PROFILE ---
    fun saveName(name: String) = sharedPreferences.edit { putString("name", name) }
    fun getName(): String = sharedPreferences.getString("name", "Người chơi") ?: "Người chơi"

    fun saveAvatar(avatar: String) = sharedPreferences.edit { putString("avatar", avatar) }
    fun getAvatar(): String = sharedPreferences.getString("avatar", "avatar1") ?: "avatar1"

    fun saveBorder(borderResId: Int) = sharedPreferences.edit { putInt("frame", borderResId) }
    fun getBorder(): Int = sharedPreferences.getInt("frame", R.drawable.bg_avatar_border_fancy)

    // --- CURRENCY ---
    fun saveCoins(coins: Int) = sharedPreferences.edit { putInt("current_coins", coins) }
    fun getCoins(): Int = sharedPreferences.getInt("current_coins", 0)
    fun addCoin(amount: Int) = saveCoins(getCoins() + amount)

    fun saveXu(xu: Int) = sharedPreferences.edit { putInt("current_xu", xu) }
    fun getXu(): Int = sharedPreferences.getInt("current_xu", 0)
    fun addXu(amount: Int) = saveXu(getXu() + amount)

    fun saveExp(exp: Int) = sharedPreferences.edit { putInt("current_exp", exp) }
    fun getExp(): Int = sharedPreferences.getInt("current_exp", 0)
    fun addExp(amount: Int) = saveExp(getExp() + amount)

    // --- LEVEL ---
    fun getCurrentUnlockedLevel(): Int = sharedPreferences.getInt("CURRENT_UNLOCKED_LEVEL", 1)
    fun setLevelUnlocked(level: Int) {
        val current = getCurrentUnlockedLevel()
        if (level > current) {
            sharedPreferences.edit { putInt("CURRENT_UNLOCKED_LEVEL", level) }
        }
    }

    fun getLevelScore(levelId: Int): Int = mapPrefs.getInt("SCORE_$levelId", 0)
    fun saveLevelScore(levelId: Int, score: Int) = mapPrefs.edit { putInt("SCORE_$levelId", score) }

    // --- HEARTS ---
    fun getHearts() = sharedPreferences.getInt("current_hearts", 5)
    fun addHearts(amount: Int) {
        val next = (getHearts() + amount).coerceAtMost(5)
        sharedPreferences.edit { putInt("current_hearts", next) }
        if (next >= 5) sharedPreferences.edit { putLong("last_heart_loss_time", 0L) }
    }
    fun useHeart() {
        val current = getHearts()
        if (current > 0) {
            sharedPreferences.edit { putInt("current_hearts", current - 1) }
            if (current == 5) sharedPreferences.edit { putLong("last_heart_loss_time", System.currentTimeMillis()) }
        }
    }

    fun autoRegenerateHearts(): Long {
        val current = getHearts()
        if (current >= 5) return 0
        val lastTime = sharedPreferences.getLong("last_heart_loss_time", 0L)
        if (lastTime == 0L) return 0
        
        val diff = System.currentTimeMillis() - lastTime
        val interval = 180000L // 3 mins
        val recovered = (diff / interval).toInt()
        
        if (recovered > 0) {
            val next = (current + recovered).coerceAtMost(5)
            sharedPreferences.edit {
                putInt("current_hearts", next)
                putLong("last_heart_loss_time", if (next < 5) lastTime + (recovered * interval) else 0L)
            }
        }
        return if (getHearts() < 5) interval - (System.currentTimeMillis() - sharedPreferences.getLong("last_heart_loss_time", 0L)) % interval else 0
    }

    // --- OTHERS ---
    fun getSupportQuantity(type: String) = sharedPreferences.getInt(type, 1)
    fun addSupport(type: String, amount: Int) = sharedPreferences.edit { putInt(type, getSupportQuantity(type) + amount) }

    fun saver_va_inday(taskId: String): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return sharedPreferences.getString("task_$taskId", null) == currentDate
    }
    fun Xn_va_inday(taskId: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        sharedPreferences.edit { putString("task_$taskId", currentDate) }
    }

    fun Dk_batmo_xn(taskId: String, isReady: Boolean) = sharedPreferences.edit { putBoolean("ready_$taskId", isReady) }
    fun Dk_xacnhan_cq(taskId: String): Boolean = sharedPreferences.getBoolean("ready_$taskId", false)

    fun handleCorrectAnswer() {
        // Mỗi câu trả lời đúng tăng 10 EXP và 2 Xu Cỏ
        addExp(10)
        addXu(2)
    }

    fun handleWrongAnswer() {
        // Có thể trừ điểm hoặc reset chuỗi nếu cần
    }

    fun applyRewardByString(reward: String, levelId: Int) {
        when {
            reward.contains("50/50") -> addSupport(SUPPORT_5050, 1)
            reward.contains("Nhân đôi cơ hội") -> addSupport(SUPPORT_DOUBLE_CHANCE, 1)
            reward.contains("Đáp án đúng") -> addSupport(SUPPORT_CORRECT_ANSWER, 1)
            reward.contains("Nhân đôi điểm") -> addSupport(SUPPORT_DOUBLE_POINTS, 1)
            reward.contains("Xu") -> {
                val amount = reward.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                addXu(amount)
            }
            reward.contains("EXP") -> {
                val amount = reward.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                addExp(amount)
            }
            reward.contains("Mạng") -> addHearts(1)
            reward.contains("Điểm") -> {
                val isMinus = reward.contains("Trừ")
                val amount = reward.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                val current = getLevelScore(levelId)
                saveLevelScore(levelId, current + (if (isMinus) -amount else amount))
            }
        }
    }

    // --- PET ---
    fun getPetLevel(): Int {
        val currentPetid = getPetid()
        if(currentPetid == -1) return 1
        return sharedPreferences.getInt("pet_level_$currentPetid", 1)
    }
    fun savePetLevel(level: Int) {
        val currentPetid = getPetid()
        if (currentPetid != -1) sharedPreferences.edit { putInt("pet_level_$currentPetid", level) }
    }
    fun getPetid(): Int = sharedPreferences.getInt("pet_id", -1)
    fun savePetid(id: Int) = sharedPreferences.edit { putInt("pet_id", id) }

    fun get_sh_PetIds(): List<String> = sharedPreferences.getString("owned_pets", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    fun add_sh_Pet(id: String) {
        val sh = get_sh_PetIds().toMutableList()
        if (!sh.contains(id)) {
            sh.add(id)
            sharedPreferences.edit { putString("owned_pets", sh.joinToString(",")) }
        }
    }

    fun get_sh_EggIds(): List<String> = sharedPreferences.getString("owned_eggs", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    fun add_sh_Egg(id: String) {
        val sh = get_sh_EggIds().toMutableList()
        if (!sh.contains(id)) {
            sh.add(id)
            sharedPreferences.edit { putString("owned_eggs", sh.joinToString(",")) }
        }
    }
    fun delete_trung(id: String) {
        val sh = get_sh_EggIds().toMutableList()
        if (sh.remove(id)) sharedPreferences.edit { putString("owned_eggs", sh.joinToString(",")) }
    }
}
