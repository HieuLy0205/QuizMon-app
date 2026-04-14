package com.example.quizmon.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quizmon.R
import com.example.quizmon.ui.notification.NotificationHelper

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

            // DEBUG (biết chắc nút đã bấm)
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

            //  Lưu trước
            sharedPref.edit()
                .putString("nickname", nickname)
                .putInt("notification_hour", h)
                .putInt("notification_minute", m)
                .putBoolean("notification_enabled", true)
                .apply()

            tvSelectedTime.text = "Giờ nhắc: %02d:%02d".format(h, m)

            //  Hiện toast TRƯỚC (không bị mất)
            Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_LONG).show()

            //  Schedule an toàn (không crash)
            try {
                notificationHelper.scheduleDaily(h, m)
                Toast.makeText(this, "Đã bật nhắc!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Lỗi khi bật nhắc!", Toast.LENGTH_LONG).show()
            }
        }
    }
}