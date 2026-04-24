package com.example.quizmon.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.onboarding.AgeActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.ui.history.HistoryActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var frame: View
    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvTopics: TextView
    private lateinit var etName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)

        imgAvatar = findViewById(R.id.imgAvatar)
        frame = findViewById(R.id.frame)
        tvAge = findViewById(R.id.tvAge)
        tvGender = findViewById(R.id.tvGender)
        tvTopics = findViewById(R.id.tvTopics)
        etName = findViewById(R.id.etName)
        btnSave = findViewById(R.id.btnSave)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        setupTaskbar()
        updateHeaderStats()

        val age = prefs.getInt("age", 0)
        val gender = prefs.getString("gender", "Chưa chọn")
        val topicsSet = prefs.getStringSet("topics", emptySet())
        val name = prefs.getString("name", "")
        val avatar = prefs.getString("avatar", "avatar1")
        val savedFrame = prefs.getInt("frame", R.drawable.bg_avatar_border_fancy)

        tvAge.text = "Tuổi: $age"
        tvGender.text = "Giới tính: $gender"
        tvTopics.text = "Sở thích: ${topicsSet?.joinToString(", ") ?: "Chưa chọn"}"
        etName.setText(name)

        setAvatar(avatar ?: "avatar1")
        frame.setBackgroundResource(savedFrame)

        btnSave.setOnClickListener {
            prefs.edit().putString("name", etName.text.toString()).apply()
            Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show()
        }

        btnEditProfile.setOnClickListener {
            prefs.edit().putBoolean("profile_done", false).apply()
            startActivity(Intent(this, AgeActivity::class.java))
            finish()
        }
    }

    private fun setupTaskbar() {
        // Highlight mục Hồ sơ
        findViewById<View>(R.id.indicator_profile).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_profile).setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))

        findViewById<View>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_shop).setOnClickListener {
            startActivity(Intent(this, activity_shop::class.java))
        }

        findViewById<View>(R.id.nav_history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<View>(R.id.nav_menu).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun updateHeaderStats() {
        val pref = PreferenceManager(this)
        val textCoin = findViewById<TextView>(R.id.textcoins)
        textCoin.text = pref.getCoins().toString()
        val textXu = findViewById<TextView>(R.id.textxu)
        textXu.text = pref.getXu().toString()
    }

    private fun setAvatar(id: String) {
        when (id) {
            "avatar1" -> imgAvatar.setImageResource(R.drawable.avatar1)
            "avatar2" -> imgAvatar.setImageResource(R.drawable.avatar2)
            "avatar_vip1" -> imgAvatar.setImageResource(R.drawable.avatar_vip1)
            else -> imgAvatar.setImageResource(R.drawable.avatar1)
        }
    }

    override fun onResume() {
        super.onResume()
        updateHeaderStats()
    }
}
