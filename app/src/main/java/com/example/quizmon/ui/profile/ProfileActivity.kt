package com.example.quizmon.ui.profile

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var frame: View

    private lateinit var tvName: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvTopics: TextView

    private lateinit var btnEditProfile: MaterialButton
    private lateinit var btnEditAvatar: MaterialButton
    private lateinit var btnReset: MaterialButton

    private lateinit var layoutEdit: LinearLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etTopics: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        preferenceManager = PreferenceManager(this)

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

        btnEditProfile.setOnClickListener {
            SoundManager.playClick()
            val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)
            etName.setText(prefs.getString("name", ""))
            etAge.setText(prefs.getInt("age", 0).toString())
            etTopics.setText(prefs.getStringSet("topics", setOf())?.joinToString(", "))
            layoutEdit.visibility = if (layoutEdit.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        btnSave.setOnClickListener {
            SoundManager.playClick()
            val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)
            val newName = etName.text.toString().trim()
            val newAge = etAge.text.toString().toIntOrNull() ?: 0
            val newTopics = etTopics.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

            prefs.edit().apply {
                putString("name", newName)
                putInt("age", newAge)
                putStringSet("topics", newTopics)
                apply()
            }

            loadProfile()
            layoutEdit.visibility = View.GONE
            Toast.makeText(this, "Đã cập nhật hồ sơ!", Toast.LENGTH_SHORT).show()
        }

        btnEditAvatar.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, AvatarActivity::class.java))
        }

        btnReset.setOnClickListener {
            SoundManager.playClick()
            AlertDialog.Builder(this)
                .setTitle("Xác nhận Reset")
                .setMessage("Toàn bộ tiến trình chơi và dữ liệu sẽ bị xóa vĩnh viễn. Bạn có chắc chắn?")
                .setPositiveButton("Xóa hết") { _, _ ->
                    getSharedPreferences("QuizMonPrefs", MODE_PRIVATE).edit().clear().apply()
                    getSharedPreferences("QuizMonMapPrefs", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, AgeActivity::class.java))
                    finishAffinity()
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_profile)?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_profile)?.setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))

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

    private fun loadProfile() {
        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)
        val name = prefs.getString("name", "Người chơi")
        val age = prefs.getInt("age", 18)
        val gender = prefs.getString("gender", "Chưa rõ")
        val topics = prefs.getStringSet("topics", setOf())
        val avatar = prefs.getString("avatar", "avatar1")
        val frameRes = prefs.getInt("frame", R.drawable.bg_avatar_border_fancy)

        tvName.text = name
        tvAge.text = "Tuổi: $age"
        tvGender.text = "Giới tính: $gender"
        tvTopics.text = "Sở thích: ${if (topics.isNullOrEmpty()) "Chưa cập nhật" else topics.joinToString(", ")}"

        val resId = when (avatar) {
            "avatar1" -> R.drawable.avatar1
            "avatar2" -> R.drawable.avatar2
            "avatar_vip1" -> R.drawable.avatar_vip1
            else -> R.drawable.avatar1
        }
        imgAvatar.setImageResource(resId)
        frame.setBackgroundResource(frameRes)
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
