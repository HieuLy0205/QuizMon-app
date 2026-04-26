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

    // --- QUẢN LÝ TIM (Hearts/Life) ---
    fun saveHearts(hearts: Int) {
        sharedPreferences.edit().putInt("current_hearts", hearts).apply()
    }
    fun getHearts(): Int {
        return sharedPreferences.getInt("current_hearts", 5) // Mặc định 5 tim
    }
    fun useHeart() {
        val current = getHearts()
        if (current > 0) saveHearts(current - 1)
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
}
