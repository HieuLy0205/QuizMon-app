package com.example.quizmon.utils
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.nio.file.Files.delete
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


    // --- ĐIỂM ẢI (Stage Score) ---
    fun getLevelScore(levelId: Int) = mapPrefs.getInt("SCORE_$levelId", 0)
    
    // Sử dụng commit = true để đảm bảo dữ liệu được ghi ngay lập tức (quan trọng cho vòng quay)
    fun saveLevelScore(levelId: Int, score: Int) = mapPrefs.edit(commit = true) { 
        putInt("SCORE_$levelId", score) 
    }
    // --- ĐIỂM ẢI (Stage Score) ---
    
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
    fun getHearts() = sharedPreferences.getInt("current_hearts", 5)
    fun addHearts(amount: Int) {
        val next = getHearts() + amount
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

    fun getSupportQuantity(type: String) = sharedPreferences.getInt(type, 1)
    fun addSupport(type: String, amount: Int) = sharedPreferences.edit { putInt(type, getSupportQuantity(type) + amount) }


    // --- QUẢN LÝ NHIỆM VỤ ---
    fun saver_va_inday(taskId: String): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastData = sharedPreferences.getString("task_$taskId", null)
        return lastData == currentDate
    }
    fun Xn_va_inday(taskId: String) {
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
    }

    // --- QUẢN LÝ DANH SÁCH SỞ HỮU ---
    fun get_sh_PetIds(): List<String> {
        val sh_Str = sharedPreferences.getString("owned_pets", "") ?: ""
        return if (sh_Str.isEmpty()) emptyList() else sh_Str.split(",")
    }

    fun add_sh_Pet(id: String) {
        val sh = get_sh_PetIds().toMutableList()
        if (!sh.contains(id)) {
            sh.add(id)
            sharedPreferences.edit().putString("owned_pets", sh.joinToString(",")).apply()
        }
    }

    fun get_sh_EggIds(): List<String> {
        val sh_Str = sharedPreferences.getString("owned_eggs", "") ?: ""
        return if (sh_Str.isEmpty()) emptyList() else sh_Str.split(",")
    }

    fun add_sh_Egg(id: String) {
        val sh = get_sh_EggIds().toMutableList()
        if (!sh.contains(id)) {
            sh.add(id)
            sharedPreferences.edit().putString("owned_eggs", sh.joinToString(",")).apply()
        }
    }

    fun delete_trung(id: String) {
        val sh = get_sh_EggIds().toMutableList()
        if (sh.remove(id)) {
            sharedPreferences.edit().putString("owned_eggs", "").apply()
        }
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
