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
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.TaskHeadManager

class activity_shop : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop_main)

        preferenceManager = PreferenceManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupTaskbar()
        
        findViewById<Button>(R.id.btn_goi_api1).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, shop_tim::class.java))
        }
        findViewById<Button>(R.id.btn_goi_api2).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, shop_xu::class.java))
        }
        findViewById<Button>(R.id.btn_goi_api3).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, shop_phobien::class.java))
        }
        findViewById<Button>(R.id.btn_goi_api4).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, shop_pvp::class.java))
        }
    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_shop).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_shop).setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))
        
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            SoundManager.playClick()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener { 
            SoundManager.playClick()
            startActivity(Intent(this, ProfileActivity::class.java)) 
        }
        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener { 
            SoundManager.playClick()
            startActivity(Intent(this, HistoryActivity::class.java)) 
        }
        findViewById<LinearLayout>(R.id.nav_shop).setOnClickListener { /* Already here */ }
        findViewById<LinearLayout>(R.id.nav_menu).setOnClickListener { 
            SoundManager.playClick()
            startActivity(Intent(this, SettingsActivity::class.java)) 
        }
    }

    override fun onResume() {
        super.onResume()
        //Bắt đầu đếm ngược Header tự động
        TaskHeadManager.startLoop(findViewById(R.id.layout_taskhead), preferenceManager)
        
        // Phát nhạc nền
        SoundManager.playMusic(this, R.raw.background)
    }
    
    override fun onPause() {
        super.onPause()
        //Dừng đếm ngược
        TaskHeadManager.stopLoop()
        
        SoundManager.pauseMusic()
    }
}
