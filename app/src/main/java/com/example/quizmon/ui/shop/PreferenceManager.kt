package com.example.quizmon.ui.shop

import android.adservices.adid.AdId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
    fun saveCoins(coins: Int) {
        sharedPreferences.edit().putInt("current_coins", coins).apply()
    }
    //truy cập xml sharedpreferences trên bộ nhớ để lấy coin
    fun getCoins(): Int {
        return sharedPreferences.getInt("current_coins", 0)
    }

    fun addCoin(amount: Int) {
        val current = getCoins()
        //savecoins là ghi đề
        saveCoins(current + amount) // CTTT CỘNG THÊM SỐ LƯỢNG MỚI VÀO SỐ LƯỢNG CỦ
    }

    fun saveXu(xu: Int) {
        sharedPreferences.edit().putInt("current_xu", xu).apply()
    }
    fun getXu(): Int {
        return sharedPreferences.getInt("current_xu", 0)
    }
    fun addXu(amount: Int) {
        val current = getXu()
        saveXu(current + amount)
    }

    //hàm kiêm tra nhiệm vụ trong ngày
    fun isTaskCompletedToday(taskId: String): Boolean {
        val lastData = sharedPreferences.getString("task_$taskId", null)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return lastData == currentDate
    }
    //đánh màu xám dấu hiệu hoàn thành nhiệm vụ
    fun markTaskCompletedToday(taskId: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        sharedPreferences.edit().putString("task_$taskId", currentDate).apply()
    }
    //hàm đánh dấu hoàn thành song cả ải nhỏ
    fun Dk_Ainho_Addcoin(taskId: String, isReady: Boolean) {
        sharedPreferences.edit().putBoolean("task_$taskId", isReady).apply()
    }
    //hàm xem là song ải nhưng chưa nhận coin
    fun Dk_Ainho_coin(taskId: String): Boolean {
        return sharedPreferences.getBoolean("task_$taskId", false)
    }
    fun getPetLevel(): Int{
        return sharedPreferences.getInt("pet_level", 1)
    }
    fun savePetLevel(level: Int){
        sharedPreferences.edit().putInt("pet_level", level).apply()
    }
}