package com.example.quizmon.ui.profile

import android.content.Context
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
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.pet.PetActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.utils.StreakManager

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupTaskbar()
        updateUI()
    }

    private fun updateUI() {
        val prefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
        val preferenceManager = PreferenceManager(this)
        val streakManager = StreakManager(this)

        findViewById<TextView>(R.id.textcoin)?.text = preferenceManager.getCoins().toString()
        findViewById<TextView>(R.id.tvCoins)?.text = prefs.getInt("current_coins", 0).toString()
        
        findViewById<TextView>(R.id.tvStatStreak).text = streakManager.getCurrentStreak().toString()
        findViewById<TextView>(R.id.tvStatLevel).text = prefs.getInt("CURRENT_UNLOCKED_LEVEL", 1).toString()
    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_profile).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_profile).setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))

        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            // Already here
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
}