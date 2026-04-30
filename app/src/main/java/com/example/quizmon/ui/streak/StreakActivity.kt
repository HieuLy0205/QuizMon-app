package com.example.quizmon.ui.streak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.data.repository.StatisticsRepository
import com.example.quizmon.ui.statistics.StatisticsFragment
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.utils.TaskHeadManager

class StreakActivity : AppCompatActivity() {

    private lateinit var layoutThanhTich: View
    private lateinit var fragmentContainer: View
    private lateinit var indicatorThanhTich: View
    private lateinit var indicatorThongKe: View
    private lateinit var tvTabThanhTich: TextView
    private lateinit var tvTabThongKe: TextView
    private lateinit var ivTabThanhTich: ImageView
    private lateinit var ivTabThongKe: ImageView
    
    // Views cho Thành tích
    private lateinit var tvPercentWarrior: TextView
    private lateinit var pbWarrior: ProgressBar
    private lateinit var tvPercentFan: TextView
    private lateinit var pbFan: ProgressBar
    private lateinit var tvPercentHunter: TextView
    private lateinit var pbHunter: ProgressBar
    private lateinit var tvPercentPassion: TextView
    private lateinit var pbPassion: ProgressBar

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var statisticsRepository: StatisticsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Đồng bộ với chuẩn MainActivity
        enableEdgeToEdge()
        setContentView(R.layout.activity_streak)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.streak_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)
        statisticsRepository = StatisticsRepository(this)
        
        initViews()
        setupClickListeners()
        setupTaskbar()
        
        // Mặc định ban đầu sẽ tải dữ liệu Thành tích
        loadAchievementData()
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

        // Ánh xạ các view Thành tích
        tvPercentWarrior = findViewById(R.id.tvPercentWarrior)
        pbWarrior = findViewById(R.id.pbWarrior)
        tvPercentFan = findViewById(R.id.tvPercentFan)
        pbFan = findViewById(R.id.pbFan)
        tvPercentHunter = findViewById(R.id.tvPercentHunter)
        pbHunter = findViewById(R.id.pbHunter)
        tvPercentPassion = findViewById(R.id.tvPercentPassion)
        pbPassion = findViewById(R.id.pbPassion)
    }

    private fun loadAchievementData() {
        val achievements = statisticsRepository.getAchievements()
        
        // Cập nhật UI dựa trên dữ liệu từ Repository (nếu chưa chơi sẽ là 0)
        achievements.forEach { achievement ->
            val percent = (achievement.currentProgress * 100) / achievement.maxProgress
            val displayPercent = if (percent > 100) 100 else percent
            
            when (achievement.id) {
                "1" -> { // Chiến binh
                    tvPercentWarrior.text = "$displayPercent%"
                    pbWarrior.progress = displayPercent
                }
                "2" -> { // Người hâm mộ
                    tvPercentFan.text = "$displayPercent%"
                    pbFan.progress = displayPercent
                }
                "3" -> { // Thợ săn
                    tvPercentHunter.text = "$displayPercent%"
                    pbHunter.progress = displayPercent
                }
                "4" -> { // Đam mê
                    tvPercentPassion.text = "$displayPercent%"
                    pbPassion.progress = displayPercent
                }
            }
        }
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
        
        // Tải lại dữ liệu mỗi khi hiện tab
        loadAchievementData()
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

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
        loadAchievementData() // Cập nhật lại khi quay lại màn hình
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
}