package com.example.quizmon.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.faq.FaqActivity
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.ui.notification.NotificationHelper
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.TaskHeadManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationHelper: NotificationHelper
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        preferenceManager = PreferenceManager(this)

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
        setupTaskbar()

        findViewById<Button>(R.id.btnReport).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, FaqActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật Header và đếm ngược Tim
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
        
        // Phát nhạc nền
        SoundManager.playMusic(this, R.raw.background)
    }

    override fun onPause() {
        super.onPause()
        // Dừng cập nhật Header
        TaskHeadManager.stopLoop()
        
        SoundManager.pauseMusic()
    }

    private fun setupTaskbar() {
        // Highlight tab Menu (Settings)
        findViewById<View>(R.id.indicator_menu)?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_menu)?.setTextColor(
            ContextCompat.getColor(this, R.color.taskbar_active)
        )

        findViewById<LinearLayout>(R.id.nav_home)?.setOnClickListener {
            SoundManager.playClick()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        findViewById<LinearLayout>(R.id.nav_history)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_shop)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, activity_shop::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_profile)?.setOnClickListener {
            SoundManager.playClick()
            openProfileFlow()
        }
    }

    private fun openProfileFlow() {
        val prefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("FIRST_TIME", true)
        if (isFirstTime) {
            startActivity(Intent(this, com.example.quizmon.ui.onboarding.AgeActivity::class.java))
        } else {
            startActivity(Intent(this, ProfileActivity::class.java))
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

        tabSettings.setOnClickListener { 
            SoundManager.playClick()
            showSettings() 
        }
        tabReminder.setOnClickListener { 
            SoundManager.playClick()
            showReminder() 
        }
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
            SoundManager.playClick()
            TimePickerDialog(this, { _, h, m ->
                etHour.setText("%02d".format(h))
                etMinute.setText("%02d".format(m))
                tvTime.text = "Giờ nhắc: %02d:%02d".format(h, m)
            }, hour, minute, true).show()
        }

        btnSave.setOnClickListener {
            SoundManager.playClick()
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

        toggle(findViewById(R.id.btnSound), "sound", prefs, true) { isOn ->
            SoundManager.setSoundEnabled(isOn)
        }
        toggle(findViewById(R.id.btnMusic), "music", prefs, true) { isOn ->
            SoundManager.setMusicEnabled(isOn)
        }
        toggle(findViewById(R.id.btnVibrate), "vibrate", prefs, true) { isOn ->
            SoundManager.setVibrateEnabled(isOn)
        }
        toggle(findViewById(R.id.btnLargeText), "bigtext", prefs, false) { isOn ->
            applyTextSize(isOn)
        }
        toggle(findViewById(R.id.btnDarkMode), "darkmode", prefs, false) { isOn ->
            applyDarkMode(isOn)
        }
    }

    private fun toggle(view: View, key: String, prefs: android.content.SharedPreferences, default: Boolean, onToggle: ((Boolean) -> Unit)? = null) {
        var isOn = prefs.getBoolean(key, default)

        updateToggleUI(view, isOn)

        view.setOnClickListener {
            SoundManager.playClick()
            isOn = !isOn
            prefs.edit().putBoolean(key, isOn).apply()
            updateToggleUI(view, isOn)
            onToggle?.invoke(isOn)
        }
    }

    private fun updateToggleUI(view: View, isOn: Boolean) {
        view.alpha = if (isOn) 1f else 0.4f
    }

    private fun applyDarkMode(isOn: Boolean) {
        if (isOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun applyTextSize(isOn: Boolean) {
        // Saving is handled by toggle()
        
        // Recreate activity to apply font scale changes immediately via QuizMonApp's lifecycle callback
        recreate()
    }
}
