package com.example.quizmon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.ui.pet.PetActivity
import com.example.quizmon.ui.level.LevelMapActivity
import com.example.quizmon.ui.shop.activity_shop

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //  Bắt sự kiện nút Quiz -> Mở LevelMapActivity thay vì QuizActivity trực tiếp
        val btnQuiz = findViewById<Button>(R.id.btnQuiz)
        btnQuiz.setOnClickListener {
            val intent = Intent(this, LevelMapActivity::class.java)
            startActivity(intent)
        }

        // Bắt sự kiện nút Pet
        val btnPet = findViewById<Button>(R.id.btnPet)
        btnPet.setOnClickListener {
            val intent = Intent(this, PetActivity::class.java)
            startActivity(intent)
        }
        // Bắt sự kiện nút shop
        val btnShop = findViewById<Button>(R.id.btnShop)
        btnShop.setOnClickListener {
            val intent = Intent(this, activity_shop::class.java)
            startActivity(intent)
        }
    }
}