package com.example.quizmon

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.ui.pet.PetActivity
import com.example.quizmon.ui.shop.shop_phobien
import com.example.quizmon.ui.level.LevelMapActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.ui.streak.StreakActivity
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.utils.StreakManager
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private var dX = 0f
    private var dY = 0f
    private val CLICK_DRAG_TOLERANCE = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupTaskbar()
        updateUI()
        setupFloatingPet()

        findViewById<View>(R.id.btnQuiz).setOnClickListener {
            startActivity(Intent(this, LevelMapActivity::class.java))
        }
        
        findViewById<View>(R.id.cardDailyReward).setOnClickListener {
            startActivity(Intent(this, shop_phobien::class.java))
        }

        findViewById<FrameLayout>(R.id.layoutStreak)?.setOnClickListener {
            startActivity(Intent(this, StreakActivity::class.java))
        }

        findViewById<FrameLayout>(R.id.layoutStreak)?.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.streak_bounce)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingPet() {
        val ivFloatingPet = findViewById<ImageView>(R.id.ivFloatingPet)
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.pet_bounce)
        ivFloatingPet.startAnimation(bounceAnimation)

        ivFloatingPet.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    view.clearAnimation()
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate().x(event.rawX + dX).y(event.rawY + dY).setDuration(0).start()
                }
                MotionEvent.ACTION_UP -> {
                    view.startAnimation(bounceAnimation)
                    if (abs(view.x - (event.rawX + dX)) < CLICK_DRAG_TOLERANCE &&
                        abs(view.y - (event.rawY + dY)) < CLICK_DRAG_TOLERANCE) {
                        view.performClick()
                    }
                }
                else -> return@setOnTouchListener false
            }
            true
        }

        ivFloatingPet.setOnClickListener {
            startActivity(Intent(this, PetActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val prefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
        val preferenceManager = PreferenceManager(this)
        val streakManager = StreakManager(this)

        val currentLevel = prefs.getInt("CURRENT_UNLOCKED_LEVEL", 1)
        findViewById<TextView>(R.id.tvCurrentLevel)?.text = currentLevel.toString()

        // Cập nhật đúng ID mới từ layout_taskhead
        val textCoin = findViewById<TextView>(R.id.head_text_coin)
        textCoin.text = preferenceManager.getCoins().toString()
        val textXu = findViewById<TextView>(R.id.head_text_xu)
        textXu.text = preferenceManager.getXu().toString()

    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_home)?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_home)?.setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))

        findViewById<LinearLayout>(R.id.nav_history)?.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        findViewById<LinearLayout>(R.id.nav_shop)?.setOnClickListener { startActivity(Intent(this, activity_shop::class.java)) }
        findViewById<LinearLayout>(R.id.nav_menu)?.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        findViewById<LinearLayout>(R.id.nav_profile)?.setOnClickListener { openProfileFlow() }
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
}
