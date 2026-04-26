package com.example.quizmon.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.utils.TaskHeadManager

class HistoryActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    
    //Handler để cập nhật đồng hồ Tim liên tục mỗi giây
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateUI()
            updateHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        
        preferenceManager = PreferenceManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupTaskbar()
        updateUI()
    }

    private fun updateUI() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_history).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_history).setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))

        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
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
        updateUI()
        updateHandler.post(updateRunnable) //Bắt đầu đếm giây
    }
    
    override fun onPause() {
        super.onPause()
        updateHandler.removeCallbacks(updateRunnable) //Dừng đếm giây
    }
}
