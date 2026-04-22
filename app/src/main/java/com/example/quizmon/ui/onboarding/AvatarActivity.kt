package com.example.quizmon.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.activity_shop

class AvatarActivity : AppCompatActivity() {

    private lateinit var imgAvatar: ImageView
    private lateinit var frame: View

    private var selectedAvatar = "avatar1"
    private var selectedFrame = R.drawable.bg_avatar_border_fancy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)

        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)

        setupTaskbar()

        imgAvatar = findViewById(R.id.imgPreview)
        frame = findViewById(R.id.framePreview)

        val btnAvatar1 = findViewById<Button>(R.id.btnAvatar1)
        val btnAvatar2 = findViewById<Button>(R.id.btnAvatar2)
        val btnAvatarVip = findViewById<Button>(R.id.btnAvatarVip)

        val btnFrame1 = findViewById<Button>(R.id.btnFrame1)
        val btnFrame2 = findViewById<Button>(R.id.btnFrame2)
        val btnFrame3 = findViewById<Button>(R.id.btnFrame3)

        val btnDone = findViewById<Button>(R.id.btnFinish)

        // LOAD
        selectedAvatar = prefs.getString("avatar", "avatar1") ?: "avatar1"
        selectedFrame = prefs.getInt("frame", R.drawable.bg_avatar_border_fancy)

        setAvatar(selectedAvatar)
        frame.setBackgroundResource(selectedFrame)

        // AVATAR
        btnAvatar1.setOnClickListener {
            selectedAvatar = "avatar1"
            setAvatar(selectedAvatar)
        }

        btnAvatar2.setOnClickListener {
            selectedAvatar = "avatar2"
            setAvatar(selectedAvatar)
        }

        btnAvatarVip.setOnClickListener {
            val unlocked = prefs.getBoolean("vip_unlock", false)
            if (unlocked) {
                selectedAvatar = "avatar_vip1"
                setAvatar(selectedAvatar)
            } else {
                Toast.makeText(this, "Chưa mở khóa VIP", Toast.LENGTH_SHORT).show()
            }
        }

        // FRAME
        btnFrame1.setOnClickListener {
            selectedFrame = R.drawable.bg_avatar_border_pikachu
            frame.setBackgroundResource(selectedFrame)
        }

        btnFrame2.setOnClickListener {
            selectedFrame = R.drawable.bg_avatar_border_kyogre
            frame.setBackgroundResource(selectedFrame)
        }

        btnFrame3.setOnClickListener {
            selectedFrame = R.drawable.bg_avatar_border_rayquaza
            frame.setBackgroundResource(selectedFrame)
        }

        // DONE
        btnDone.setOnClickListener {

            prefs.edit()
                .putString("avatar", selectedAvatar)
                .putInt("frame", selectedFrame)
                .putBoolean("FIRST_TIME", false)
                .apply()

            startActivity(Intent(this, ProfileActivity::class.java))
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

    private fun setAvatar(id: String) {
        when (id) {
            "avatar1" -> imgAvatar.setImageResource(R.drawable.avatar1)
            "avatar2" -> imgAvatar.setImageResource(R.drawable.avatar2)
            "avatar_vip1" -> imgAvatar.setImageResource(R.drawable.avatar_vip1)
            else -> imgAvatar.setImageResource(R.drawable.avatar1)
        }
    }
}