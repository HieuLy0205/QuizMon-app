package com.example.quizmon.utils

import android.view.View
import android.widget.TextView
import com.example.quizmon.R
import com.example.quizmon.ui.shop.PreferenceManager

object TaskHeadManager {
    /**
     * Hàm cập nhật toàn bộ chỉ số cho thanh TaskHead
     */
    fun update(taskHeadRoot: View?, preferenceManager: PreferenceManager) {
        if (taskHeadRoot == null) return

        val tvStar = taskHeadRoot.findViewById<TextView>(R.id.textcoins)
        val tvCoin = taskHeadRoot.findViewById<TextView>(R.id.textxu)
        val tvExp = taskHeadRoot.findViewById<TextView>(R.id.head_text_exp)
        val tvHeartCount = taskHeadRoot.findViewById<TextView>(R.id.head_text_heart_count)
        val tvHeartTime = taskHeadRoot.findViewById<TextView>(R.id.head_text_heart)

        tvStar?.text = preferenceManager.getCoins().toString()
        tvCoin?.text = preferenceManager.getXu().toString()
        tvExp?.text = preferenceManager.getExp().toString()
        
        // --- XỬ LÝ TIM ---
        // 1. Tự động hồi tim dựa trên thời gian thực
        val remainingMs = preferenceManager.autoRegenerateHearts()
        val currentHearts = preferenceManager.getHearts()
        
        tvHeartCount?.text = currentHearts.toString()
        
        // 2. Hiển thị trạng thái đếm ngược hoặc MAX
        if (currentHearts >= 5) {
            tvHeartTime?.text = "MAX"
        } else {
            // Chuyển đổi ms thành định dạng mm:ss
            val totalSeconds = remainingMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            tvHeartTime?.text = String.format("%02d:%02d", minutes, seconds)
        }
    }
}
