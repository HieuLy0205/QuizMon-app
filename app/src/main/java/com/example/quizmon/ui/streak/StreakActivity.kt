package com.example.quizmon.ui.streak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.ui.statistics.StatisticsFragment
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.ui.profile.ProfileActivity

class StreakActivity : AppCompatActivity() {

    private lateinit var layoutThanhTich: View
    private lateinit var fragmentContainer: View
    private lateinit var indicatorThanhTich: View
    private lateinit var indicatorThongKe: View
    private lateinit var tvTabThanhTich: TextView
    private lateinit var tvTabThongKe: TextView
    private lateinit var ivTabThanhTich: ImageView
    private lateinit var ivTabThongKe: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streak)

        initViews()
        setupClickListeners()
        setupTaskbar()
        updateHeaderStats()
        
        showThanhTich()
    }

    private fun initViews() {
        layoutThanhTich = findViewById(R.id.layoutThanhTich)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        indicatorThanhTich = findViewById(R.id.indicatorThanhTich)
        indicatorThongKe = findViewById(R.id.indicatorThongKe)
        tvTabThanhTich = findViewById(R.id.tvTabThanhTich)
        tvTabThongKe = findViewById(R.id.tvTabThongKe)
        ivTabThanhTich = findViewById(R.id.ivTabThanhTich)
        ivTabThongKe = findViewById(R.id.ivTabThongKe)
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<LinearLayout>(R.id.tabThanhTich).setOnClickListener { showThanhTich() }
        findViewById<LinearLayout>(R.id.tabThongKe).setOnClickListener { showThongKe() }
    }

    private fun setupTaskbar() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_shop).setOnClickListener {
            startActivity(Intent(this, activity_shop::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_menu).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun showThanhTich() {
        layoutThanhTich.visibility = View.VISIBLE
        fragmentContainer.visibility = View.GONE
        indicatorThanhTich.visibility = View.VISIBLE
        indicatorThongKe.visibility = View.INVISIBLE
        tvTabThanhTich.setTextColor(resources.getColor(R.color.black))
        tvTabThongKe.setTextColor(resources.getColor(R.color.taskbar_text))
        ivTabThanhTich.alpha = 1.0f
        ivTabThongKe.alpha = 0.4f
    }

    private fun showThongKe() {
        layoutThanhTich.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
        indicatorThanhTich.visibility = View.INVISIBLE
        indicatorThongKe.visibility = View.VISIBLE
        tvTabThanhTich.setTextColor(resources.getColor(R.color.taskbar_text))
        tvTabThongKe.setTextColor(resources.getColor(R.color.black))
        ivTabThanhTich.alpha = 0.4f
        ivTabThongKe.alpha = 1.0f

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, StatisticsFragment.newInstance())
            .commit()
    }

    private fun updateHeaderStats() {
        val pref = PreferenceManager(this)
        val textCoin = findViewById<TextView>(R.id.textcoins)
        textCoin.text = pref.getCoins().toString()
        val textXu = findViewById<TextView>(R.id.textxu)
        textXu.text = pref.getXu().toString()
    }

    override fun onResume() {
        super.onResume()
        updateHeaderStats()
    }
}
