package com.example.quizmon.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.activity_shop

class AgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.age_activity)

        val seekAge = findViewById<SeekBar>(R.id.seekAge)
        val tvAge = findViewById<TextView>(R.id.tvAge)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        setupTaskbar()

        tvAge.text = seekAge.progress.toString()

        seekAge.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvAge.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)
            prefs.edit()
                .putInt("age", seekAge.progress)
                .apply()

            startActivity(Intent(this@AgeActivity, PersonalizeActivity::class.java))
            finish()
        }
    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<View>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.nav_shop).setOnClickListener {
            startActivity(Intent(this, activity_shop::class.java))
        }

        findViewById<View>(R.id.nav_menu).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}