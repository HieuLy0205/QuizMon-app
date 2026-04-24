package com.example.quizmon.ui.streak

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import com.example.quizmon.ui.statistics.StatisticsActivity

class StreakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streak)

        // 1. Xử lý nút Back (Mũi tên đen có sẵn)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish() // Quay về màn hình chính
        }

        // 2. Xử lý chuyển sang Thống kê khi nhấn vào Tab Thống kê
        val tabLayout = findViewById<LinearLayout>(R.id.tabLayout)
        // Tab Layout của bạn có 3 con: Thành tích (0), Thống kê (1), Mua hàng (2)
        val tabStatistics = tabLayout.getChildAt(1)

        tabStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
    }
}