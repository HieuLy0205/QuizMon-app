package com.example.quizmon.ui.level

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import kotlin.random.Random

class TreasureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treasure)

        val chests = listOf(
            findViewById<ImageView>(R.id.chest1),
            findViewById<ImageView>(R.id.chest2),
            findViewById<ImageView>(R.id.chest3)
        )

        chests.forEach { chest ->
            chest.setOnClickListener {
                openChest()
            }
        }
    }

    private fun openChest() {
        val rewards = listOf("100 Xu", "1 Lượt chơi", "50 Kim cương", "Mảnh thú cưng")
        val reward = rewards.random()
        Toast.makeText(this, "Chúc mừng! Bạn nhận được: $reward", Toast.LENGTH_LONG).show()
        
        // Đợi 1 chút rồi quay lại bản đồ
        android.os.Handler(mainLooper).postDelayed({
            finish()
        }, 1500)
    }
}
