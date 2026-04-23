package com.example.quizmon.ui.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dùng đúng layout khung chứa mà mình đã tạo cho bạn
        setContentView(R.layout.activity_statistics)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatisticsFragment.newInstance())
                .commit()
        }
    }
}