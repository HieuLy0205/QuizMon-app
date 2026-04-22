package com.example.quizmon.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.activity_shop

class NicknameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val etName = findViewById<EditText>(R.id.etName)
        val btnNext = findViewById<Button>(R.id.btnNext)

        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)

        setupTaskbar()

        btnBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            val name = etName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập biệt danh", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString("name", name)
                .apply()

            startActivity(Intent(this, AvatarActivity::class.java))
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