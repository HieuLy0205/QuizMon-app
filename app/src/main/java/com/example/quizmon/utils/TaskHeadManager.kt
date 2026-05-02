package com.example.quizmon.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import java.util.Locale

object TaskHeadManager {
    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    /**
     * Hàm cập nhật dữ liệu lên UI (giữ nguyên logic cũ)
     */
    fun update(taskHeadRoot: View?, preferenceManager: PreferenceManager) {
        if (taskHeadRoot == null) return

        val tvStar = taskHeadRoot.findViewById<TextView>(R.id.  textcoins)
        val tvCoin = taskHeadRoot.findViewById<TextView>(R.id.textxu)
        val tvExp = taskHeadRoot.findViewById<TextView>(R.id.head_text_exp)
        val tvHeartCount = taskHeadRoot.findViewById<TextView>(R.id.head_text_heart_count)
        val tvHeartTime = taskHeadRoot.findViewById<TextView>(R.id.head_text_heart)

        tvStar?.text = preferenceManager.getCoins().toString()
        tvCoin?.text = preferenceManager.getXu().toString()
        tvExp?.text = preferenceManager.getExp().toString()

        val remainingMs = preferenceManager.autoRegenerateHearts()
        val currentHearts = preferenceManager.getHearts()
        tvHeartCount?.text = currentHearts.toString()

        if (currentHearts >= 5) {
            tvHeartTime?.text = "MAX"
        } else {
            val totalSeconds = remainingMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            tvHeartTime?.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    /**
     * Bắt đầu vòng lặp cập nhật tự động (Dùng cho onResume)
     */
    fun startLoop(taskHeadRoot: View?, preferenceManager: PreferenceManager) {
        stopLoop() // Đảm bảo không có loop nào chạy song song
        updateRunnable = object : Runnable {
            override fun run() {
                update(taskHeadRoot, preferenceManager)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateRunnable!!)
    }

    /**
     * Dừng vòng lặp cập nhật (Dùng cho onPause)
     */
    fun stopLoop() {
        updateRunnable?.let { handler.removeCallbacks(it) }
        updateRunnable = null
    }
}