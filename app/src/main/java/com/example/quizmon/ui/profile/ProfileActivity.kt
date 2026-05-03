package com.example.quizmon.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.ui.onboarding.AgeActivity
import com.example.quizmon.ui.onboarding.AvatarActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.TaskHeadManager

class ProfileActivity : AppCompatActivity() {

    //  Swipe variables
    private var x1 = 0f
    private var x2 = 0f

    private lateinit var imgAvatar: ImageView
    private lateinit var frame: View

    private lateinit var tvName: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvTopics: TextView

    private lateinit var btnEditProfile: Button
    private lateinit var btnEditAvatar: Button
    private lateinit var btnReset: Button

    private lateinit var layoutEdit: LinearLayout
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etTopics: EditText
    private lateinit var btnSave: Button

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        preferenceManager = PreferenceManager(this)
        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)

        imgAvatar = findViewById(R.id.imgAvatar)
        frame = findViewById(R.id.frame)

        tvName = findViewById(R.id.tvName)
        tvAge = findViewById(R.id.tvAge)
        tvGender = findViewById(R.id.tvGender)
        tvTopics = findViewById(R.id.tvTopics)

        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnEditAvatar = findViewById(R.id.btnEditAvatar)
        btnReset = findViewById(R.id.btnReset)

        layoutEdit = findViewById(R.id.layoutEdit)
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etTopics = findViewById(R.id.etTopics)
        btnSave = findViewById(R.id.btnSave)

        loadProfile()
        setupTaskbar()

        // EDIT
        btnEditProfile.setOnClickListener {
            etName.setText(prefs.getString("name", ""))
            etAge.setText(prefs.getInt("age", 0).toString())
            etTopics.setText(
                prefs.getStringSet("topics", setOf())
                    ?.joinToString(", ")
            )
            layoutEdit.visibility = View.VISIBLE
        }

        // SAVE
        btnSave.setOnClickListener {

            val newName = etName.text.toString().trim()
            val newAge = etAge.text.toString().toIntOrNull() ?: 0

            val newTopics = etTopics.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()

            prefs.edit()
                .putString("name", newName)
                .putInt("age", newAge)
                .putStringSet("topics", newTopics)
                .apply()

            loadProfile()
            layoutEdit.visibility = View.GONE
        }

        // ĐỔI AVATAR
        btnEditAvatar.setOnClickListener {
            startActivity(Intent(this, AvatarActivity::class.java))
        }

        // RESET
        btnReset.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn muốn reset toàn bộ dữ liệu?")
                .setPositiveButton("Reset") { _, _ ->

                    prefs.edit().clear().apply()

                    startActivity(Intent(this, AgeActivity::class.java))
                    finishAffinity()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_profile)?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_profile)?.setTextColor(
            ContextCompat.getColor(this, R.color.taskbar_active)
        )

        findViewById<LinearLayout>(R.id.nav_home)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.nav_history)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.nav_shop)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, activity_shop::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.nav_menu)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    //  Swipe BACK chuẩn
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = ev.x
            }

            MotionEvent.ACTION_UP -> {
                x2 = ev.x

                // 👉 Vuốt từ trái sang phải
                if (x2 - x1 > 120) {
                    finish()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // LOAD PROFILE
    private fun loadProfile() {
        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)

        val name = prefs.getString("name", "")
        val age = prefs.getInt("age", 0)
        val gender = prefs.getString("gender", "")
        val topics = prefs.getStringSet("topics", setOf())
        val avatar = prefs.getString("avatar", "avatar1")
        val frameRes = prefs.getInt("frame", R.drawable.bg_avatar_border_fancy)

        tvName.text = if (name.isNullOrEmpty()) "Chưa đặt tên" else name
        tvAge.text = "Tuổi: $age"
        tvGender.text = "Giới tính: $gender"
        tvTopics.text = "Sở thích: ${topics?.joinToString(", ")}"

        setAvatar(avatar ?: "avatar1")
        frame.setBackgroundResource(frameRes)
    }

    // SET AVATAR
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
        loadProfile()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
}
