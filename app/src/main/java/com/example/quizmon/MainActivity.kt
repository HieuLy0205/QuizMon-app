package com.example.quizmon

import android.annotation.SuppressLint
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
import com.example.quizmon.data.repository.petReposiroty
import com.example.quizmon.ui.pet.PetActivity
import com.example.quizmon.ui.shop.DailyRewardActivity
import com.example.quizmon.ui.level.LevelMapActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.ui.streak.StreakActivity
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.history.HistoryActivity
import com.example.quizmon.ui.pet.AnimetorActivity
import com.example.quizmon.ui.rank.RankActivity
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.StreakManager
import com.example.quizmon.utils.TaskHeadManager
import kotlin.math.abs
import com.example.quizmon.ui.onboarding.AgeActivity

class MainActivity : AppCompatActivity() {
    private var dX = 0f
    private var dY = 0f
    private val CLICK_DRAG_TOLERANCE = 10f

    private lateinit var reposiroty: petReposiroty
    private lateinit var animetor: AnimetorActivity

    private lateinit var ivFloatingPet: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var ivAvatarBorder: ImageView
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        preferenceManager = PreferenceManager(this)
        
        val isFirstTime = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE).getBoolean("FIRST_TIME", true)
        if (isFirstTime) {
            startActivity(Intent(this, AgeActivity::class.java))
            finish()
            return
        }

        tvUserName = findViewById(R.id.tvUserName)
        ivAvatar = findViewById(R.id.ivAvatar)
        ivAvatarBorder = findViewById(R.id.ivAvatarBorder)
        ivFloatingPet = findViewById(R.id.ivFloatingPet)

        SoundManager.init(this)
        reposiroty = petReposiroty()
        animetor = AnimetorActivity(ivFloatingPet)

        findViewById<View>(R.id.main)?.let { v ->
            ViewCompat.setOnApplyWindowInsetsListener(v) { _, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        setupTaskbar()
        setupFloatingPet()
        
        findViewById<View>(R.id.btnQuiz).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, LevelMapActivity::class.java))
        }

        findViewById<View>(R.id.cardDailyReward).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, DailyRewardActivity::class.java))
        }

        findViewById<View>(R.id.cardMatch).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, RankActivity::class.java))
        }

        findViewById<FrameLayout>(R.id.layoutStreak)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, StreakActivity::class.java))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingPet() {
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
            SoundManager.playClick()
            startActivity(Intent(this, PetActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
        SoundManager.playMusic(this, R.raw.background)
    }

    override fun onPause() {
        super.onPause()
        animetor.stop()
        TaskHeadManager.stopLoop()
        SoundManager.pauseMusic()
    }

    private fun updateUI() {
        // Cập nhật Profile từ PreferenceManager
        tvUserName.text = preferenceManager.getName()
        setAvatarHome(preferenceManager.getAvatar())
        ivAvatarBorder.setImageResource(preferenceManager.getBorder())

        // Cập nhật Ải hiện tại
        val currentLevel = preferenceManager.getCurrentUnlockedLevel()
        findViewById<TextView>(R.id.tvCurrentLevel)?.text = currentLevel.toString()

        // Cập nhật số Ải đã vượt qua vào Streak (Biểu tượng lửa)
        val levelsPassed = (currentLevel - 1).coerceAtLeast(0)
        findViewById<TextView>(R.id.tvStreakCount)?.text = levelsPassed.toString()

        // XỬ LÝ HIỂN THỊ PET
        val savedPetId = preferenceManager.getPetid()
        val petLevel = preferenceManager.getPetLevel()

        // Nếu chưa chọn Pet (Id = -1), lấy mặc định là Pet ID "1" (Hỏa Long)
        val finalPetId = if (savedPetId == -1) "1" else savedPetId.toString()
        
        val petDetail = reposiroty.getPetById(finalPetId)
        if (petDetail != null) {
            ivFloatingPet.visibility = View.VISIBLE
            animetor.stop() // Dừng animation cũ nếu có
            animetor.starAnimetor(petDetail.copy(currentelevel = petLevel))
        } else {
            ivFloatingPet.visibility = View.GONE
        }
    }

    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_home)?.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_home)?.setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))

        findViewById<LinearLayout>(R.id.nav_history)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_shop)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, activity_shop::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_menu)?.setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_profile)?.setOnClickListener { 
            SoundManager.playClick()
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setAvatarHome(id: String) {
        val resId = when (id) {
            "avatar1" -> R.drawable.avatar1
            "avatar2" -> R.drawable.avatar2
            "avatar_vip1" -> R.drawable.avatar_vip1
            else -> R.drawable.avatar1
        }
        ivAvatar.setImageResource(resId)
    }
}
