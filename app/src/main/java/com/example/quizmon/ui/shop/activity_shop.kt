package com.example.quizmon.ui.shop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.pet.PetActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.history.HistoryActivity

class activity_shop : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupTaskbar()
        //Bắt đầu nút Rương mạng
        findViewById<Button>(R.id.btn_goi_api1).setOnClickListener {
            val intent = Intent(this, shop_tim::class.java)
            startActivity(intent)
        }
        //Bắt đầu nút Rương xu
        findViewById<Button>(R.id.btn_goi_api2).setOnClickListener {
            val intent = Intent(this, shop_xu::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btn_goi_api3).setOnClickListener {
            val intent = Intent(this, shop_phobien::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btn_goi_api4).setOnClickListener {
            val intent = Intent(this, shop_pvp::class.java)
            startActivity(intent)
        }
    }

    private fun setupTaskbar() {
        // Highlight Cửa hàng bằng thanh gỗ
        findViewById<View>(R.id.indicator_shop).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_shop).setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))
        
        // Sự kiện click cho các icon trên taskbar
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        
        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_shop).setOnClickListener {
            startActivity(Intent(this, activity_shop::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_menu).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateCoinDisplay()
        updateXuDisplay()
    }

    private fun updateXuDisplay() {
        val textxu = findViewById<TextView>(R.id.textxu)
        val preferenceManager = PreferenceManager(this)
        textxu.text = preferenceManager.getXu().toString()
    }

    private fun updateCoinDisplay() {
        val textcoin = findViewById<TextView>(R.id.textcoin)
        val preferenceManager = PreferenceManager(this)
        textcoin.text = preferenceManager.getCoins().toString()
    }

}