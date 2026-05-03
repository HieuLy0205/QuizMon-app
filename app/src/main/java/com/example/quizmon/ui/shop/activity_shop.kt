package com.example.quizmon.ui.shop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.TaskHeadManager
import com.google.android.material.card.MaterialCardView

class activity_shop : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_main)

        preferenceManager = PreferenceManager(this)

        setupTaskbar()
        
        // Shop Tim (Mua Tim bằng Sao/Xu)
        findViewById<MaterialCardView>(R.id.cardShopTim).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, shop_tim::class.java))
        }

        // Shop Sao (Nạp Sao ước bằng tiền thật)
        findViewById<MaterialCardView>(R.id.cardShopSao).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, ShopSaoActivity::class.java))
        }

        // Shop Pet (Mua Pet bằng Xu/Nhiệm vụ)
        findViewById<MaterialCardView>(R.id.cardShopPet).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, shop_pvp::class.java))
        }

        // Shop Xu (Đổi Sao ước lấy Xu Cỏ)
        findViewById<MaterialCardView>(R.id.cardShopXu).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, shop_xu_buy::class.java))
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
        findViewById<LinearLayout>(R.id.nav_menu).setOnClickListener { 
            SoundManager.playClick()
            startActivity(Intent(this, SettingsActivity::class.java)) 
        }
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.layout_taskhead), preferenceManager)
        SoundManager.playMusic(this, R.raw.background)
    }
    
    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
        SoundManager.pauseMusic()
    }
}
