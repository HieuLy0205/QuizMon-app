package com.example.quizmon.ui.level

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import kotlin.random.Random

class FlipCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_card)

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // Tạo danh sách các phần thưởng ngẫu nhiên
        val rewards = listOf("10 Xu", "20 Xu", "50 Xu", "1 Mạng", "Cộng 10 điểm", "Cộng 20 điểm")
        
        for (i in 0 until gridLayout.childCount) {
            val card = gridLayout.getChildAt(i) as Button
            card.setOnClickListener {
                val reward = rewards.random()
                card.text = reward
                card.isEnabled = false
                Toast.makeText(this, "Chúc mừng! Bạn nhận được: $reward", Toast.LENGTH_SHORT).show()
                
                // Sau khi chọn 1 thẻ thì đóng trang sau 1.5s
                android.os.Handler(mainLooper).postDelayed({ finish() }, 1500)
            }
        }
    }
}
