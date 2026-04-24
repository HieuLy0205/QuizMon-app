package com.example.quizmon.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.notification.NotificationHelper
import com.example.quizmon.ui.pet.PetActivity
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.ui.shop.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 🔔 Xin quyền notification (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        notificationHelper = NotificationHelper(this)
        
        setupTaskbar()

        val etNickname = findViewById<EditText>(R.id.etNickname)
        val btnPickTime = findViewById<Button>(R.id.btnPickTime)
        val tvSelectedTime = findViewById<TextView>(R.id.tvSelectedTime)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val etHour = findViewById<EditText>(R.id.etHour)
        val etMinute = findViewById<EditText>(R.id.etMinute)

        val sharedPref = getSharedPreferences("settings", MODE_PRIVATE)

        // Load data
        etNickname.setText(sharedPref.getString("nickname", ""))

        val hour = sharedPref.getInt("notification_hour", 8)
        val minute = sharedPref.getInt("notification_minute", 0)

        tvSelectedTime.text = "Giờ nhắc: %02d:%02d".format(hour, minute)
        etHour.setText(hour.toString().padStart(2, '0'))
        etMinute.setText(minute.toString().padStart(2, '0'))

        // ⏰ TimePicker
        btnPickTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                etHour.setText(h.toString().padStart(2, '0'))
                etMinute.setText(m.toString().padStart(2, '0'))
                tvSelectedTime.text = "Giờ nhắc: %02d:%02d".format(h, m)
            }, hour, minute, true).show()
        }

        // 💾 SAVE
        btnSave.setOnClickListener {
            Toast.makeText(this, "Đã bấm lưu", Toast.LENGTH_SHORT).show()

            val nickname = etNickname.text.toString().trim()
            val h = etHour.text.toString().trim().toIntOrNull()
            val m = etMinute.text.toString().trim().toIntOrNull()

            if (nickname.isEmpty()) {
                etNickname.error = "Nickname không được để trống!"
                return@setOnClickListener
            }

            if (h == null || m == null || h !in 0..23 || m !in 0..59) {
                Toast.makeText(this, "Giờ không hợp lệ!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            sharedPref.edit()
                .putString("nickname", nickname)
                .putInt("notification_hour", h)
                .putInt("notification_minute", m)
                .putBoolean("notification_enabled", true)
                .apply()

            tvSelectedTime.text = "Giờ nhắc: %02d:%02d".format(h, m)
            Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_LONG).show()

            try {
                notificationHelper.scheduleDaily(h, m)
                Toast.makeText(this, "Đã bật nhắc!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Lỗi khi bật nhắc!", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        updateCoinDisplay()
        updateXuDisplay()
    }
    private fun updateXuDisplay() {
        val textxu = findViewById<TextView>(R.id.textxu)
        val preferenceManager = PreferenceManager(this)
        textxu.text = preferenceManager.getXu().toString()

    }
    private fun updateCoinDisplay(){
        val textcoin = findViewById<TextView>(R.id.textcoin)
        val preferenceManager = PreferenceManager(this)
        textcoin.text = preferenceManager.getCoins().toString()
    }

    private fun setupTaskbar() {
        // Highlight Menu bằng thanh gỗ
        findViewById<View>(R.id.indicator_menu).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_menu).setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))
        
        // Sự kiện click cho các icon trên taskbar
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, PetActivity::class.java))
        }
        
        findViewById<LinearLayout>(R.id.nav_shop).setOnClickListener {
            startActivity(Intent(this, activity_shop::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_menu).setOnClickListener {
            // Đã ở Menu rồi
        }
        
        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            // Lịch sử
        }

    }
}