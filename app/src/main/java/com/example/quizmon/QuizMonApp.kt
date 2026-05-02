package com.example.quizmon

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.quizmon.utils.SoundManager

/**
 * Lớp Application tùy chỉnh để quản lý trạng thái toàn cục của ứng dụng.
 */
class QuizMonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 1. Khởi tạo SoundManager (Âm thanh, Nhạc, Rung)
        SoundManager.init(this)
        
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        // 2. Áp dụng Giao diện tối/sáng dựa trên cài đặt đã lưu
        val isDarkMode = prefs.getBoolean("darkmode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES 
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        // 3. Áp dụng Chữ lớn toàn app thông qua ActivityLifecycleCallbacks
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                applyFontScale(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                // Đảm bảo fontScale luôn được áp dụng khi quay lại activity
                applyFontScale(activity)
            }
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}

            private fun applyFontScale(activity: Activity) {
                val isLargeText = prefs.getBoolean("bigtext", false)
                val config = activity.resources.configuration
                val targetScale = if (isLargeText) 1.2f else 1.0f
                
                if (config.fontScale != targetScale) {
                    config.fontScale = targetScale
                    @Suppress("DEPRECATION")
                    activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
                }
            }
        })
    }
}
