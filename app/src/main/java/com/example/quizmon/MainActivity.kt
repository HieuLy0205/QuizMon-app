package com.example.quizmon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.ui.pet.PetActivity
import com.example.quizmon.ui.level.LevelMapActivity
import com.example.quizmon.ui.settings.SettingsActivity
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

        setupTaskbar()

        // Bắt sự kiện nút Quiz (Card chính trên màn hình)
        findViewById<View>(R.id.btnQuiz).setOnClickListener {
            val intent = Intent(this, LevelMapActivity::class.java)
            startActivity(intent)
        }

        // Bắt sự kiện nút Settings
        /*findViewById<View>(R.id.btnSettings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }*/
    }

    private fun setupTaskbar() {
        // Highlight trang chủ bằng thanh gỗ
        findViewById<View>(R.id.indicator_home).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_home).setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))
        
        // Sự kiện click cho các icon trên taskbar
        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, PetActivity::class.java))
        }
        
        findViewById<LinearLayout>(R.id.nav_shop).setOnClickListener {
            startActivity(Intent(this, activity_shop::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            // Tính năng Lịch sử
        }

        findViewById<LinearLayout>(R.id.nav_menu).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}