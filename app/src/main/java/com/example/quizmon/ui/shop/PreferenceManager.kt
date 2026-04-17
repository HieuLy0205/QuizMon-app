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
        //svecoins là ghi đề
        saveCoins(current + amount) // CTTT CỘNG THÊM SỐ LƯỢNG MỚI VÀO SỐ LƯỢNG CỦ
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
}