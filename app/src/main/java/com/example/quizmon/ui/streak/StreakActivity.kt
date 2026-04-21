package com.example.quizmon.ui.streak

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R

class StreakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streak)

        // Bổ sung sự kiện cho nút Back
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}