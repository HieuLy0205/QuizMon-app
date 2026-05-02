
package com.example.quizmon.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.R
import com.example.quizmon.ui.faq.FaqActivity
import com.example.quizmon.ui.notification.NotificationHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_root)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        notificationHelper = NotificationHelper(this)

        requestPermission()
        setupTabs()
        setupReminder()
        setupToggles()

        findViewById<Button>(R.id.btnReport).setOnClickListener {
            startActivity(Intent(this, FaqActivity::class.java))
        }
    }

    //  PERMISSION
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    //  TAB
    private fun setupTabs() {
        val tabSettings = findViewById<TextView>(R.id.tabSettings)
        val tabReminder = findViewById<TextView>(R.id.tabReminder)

        val indicatorSettings = findViewById<View>(R.id.indicatorSettings)
        val indicatorReminder = findViewById<View>(R.id.indicatorReminder)

        val accountCard = findViewById<CardView>(R.id.accountCard)
        val reminderCard = findViewById<CardView>(R.id.reminderCard)

        val btnSave = findViewById<Button>(R.id.btnSave)

        val active = ContextCompat.getColor(this, R.color.home_green)
        val inactive = Color.parseColor("#757575")

        fun showSettings() {
            accountCard.visibility = View.VISIBLE
            reminderCard.visibility = View.GONE

            tabSettings.setTextColor(active)
            tabReminder.setTextColor(inactive)

            indicatorSettings.visibility = View.VISIBLE
            indicatorReminder.visibility = View.INVISIBLE

            btnSave.visibility = View.GONE
        }

        fun showReminder() {
            accountCard.visibility = View.GONE
            reminderCard.visibility = View.VISIBLE

            tabSettings.setTextColor(inactive)
            tabReminder.setTextColor(active)

            indicatorSettings.visibility = View.INVISIBLE
            indicatorReminder.visibility = View.VISIBLE

            btnSave.visibility = View.VISIBLE
        }

        showSettings()

        tabSettings.setOnClickListener { showSettings() }
        tabReminder.setOnClickListener { showReminder() }
    }

    // REMINDER
    private fun setupReminder() {
        val etHour = findViewById<EditText>(R.id.etHour)
        val etMinute = findViewById<EditText>(R.id.etMinute)
        val tvTime = findViewById<TextView>(R.id.tvSelectedTime)
        val btnPick = findViewById<Button>(R.id.btnPickTime)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        val hour = prefs.getInt("hour", 8)
        val minute = prefs.getInt("minute", 0)

        etHour.setText("%02d".format(hour))
        etMinute.setText("%02d".format(minute))
        tvTime.text = "Giờ nhắc: %02d:%02d".format(hour, minute)

        btnPick.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                etHour.setText("%02d".format(h))
                etMinute.setText("%02d".format(m))
                tvTime.text = "Giờ nhắc: %02d:%02d".format(h, m)
            }, hour, minute, true).show()
        }

        btnSave.setOnClickListener {
            val h = etHour.text.toString().toIntOrNull()
            val m = etMinute.text.toString().toIntOrNull()

            if (h == null || m == null) {
                Toast.makeText(this, "Giờ không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putInt("hour", h)
                .putInt("minute", m)
                .apply()

            notificationHelper.scheduleDaily(h, m)

            Toast.makeText(this, "Đã lưu nhắc nhở", Toast.LENGTH_SHORT).show()
        }
    }

    //  TOGGLE
    private fun setupToggles() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        toggle(findViewById(R.id.btnSound), "sound", prefs)
        toggle(findViewById(R.id.btnMusic), "music", prefs)
        toggle(findViewById(R.id.btnVibrate), "vibrate", prefs)
        toggle(findViewById(R.id.btnLargeText), "bigtext", prefs)
        toggle(findViewById(R.id.btnDarkMode), "darkmode", prefs)
    }

    private fun toggle(view: View, key: String, prefs: android.content.SharedPreferences) {
        var isOn = prefs.getBoolean(key, true)

        updateUI(view, isOn)

        view.setOnClickListener {
            isOn = !isOn
            prefs.edit().putBoolean(key, isOn).apply()
            updateUI(view, isOn)
        }
    }

    private fun updateUI(view: View, isOn: Boolean) {
        view.alpha = if (isOn) 1f else 0.4f
    }
}