package com.example.quizmon.ui.level

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager
import kotlin.random.Random

class SpinWheelActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var luckyWheel: LuckyWheelView
    private lateinit var dialogOverlay: FrameLayout
    private lateinit var tvDialogContent: TextView
    private var isSpinning = false
    private var hasSpun = false
    private var levelId: Int = -1

    private val rewards = listOf(
        "Trừ 20 Điểm",
        "Cộng 40 Điểm",
        "Phụ trợ 50/50 x1",
        "Phụ trợ Nhân đôi cơ hội x1",
        "Phụ trợ Đáp án đúng x1",
        "Phụ trợ Nhân đôi điểm x1",
        "Cộng 10 Xu",
        "Cộng 15 EXP",
        "Chúc bạn ngày mới tốt lành"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_wheel)

        preferenceManager = PreferenceManager(this)
        levelId = intent.getIntExtra("LEVEL_ID", -1)
        
        initViews()

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            if (hasSpun) setResult(RESULT_OK) else setResult(RESULT_CANCELED)
            finish()
        }

        luckyWheel.setOnClickListener {
            if (!isSpinning && !hasSpun) {
                startSpin()
            }
        }
        
        updateHeader()
    }

    private fun initViews() {
        luckyWheel = findViewById(R.id.luckyWheel)
        dialogOverlay = findViewById(R.id.dialogOverlay)
        tvDialogContent = findViewById(R.id.tvDialogContent)
        
        findViewById<Button>(R.id.btnDialogClose).setOnClickListener {
            dialogOverlay.visibility = View.GONE
            setResult(RESULT_OK)
            finish() 
        }
    }

    private fun startSpin() {
        isSpinning = true
        
        val rewardIndex = Random.nextInt(rewards.size)
        val reward = rewards[rewardIndex]

        val anglePerItem = 360f / rewards.size
        val targetItemRotation = 360f - (rewardIndex * anglePerItem)
        val totalRotation = (360f * 5) + targetItemRotation

        val animator = ObjectAnimator.ofFloat(luckyWheel, View.ROTATION, 0f, totalRotation)
        animator.duration = 4000 
        animator.interpolator = DecelerateInterpolator()
        
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isSpinning = false
                hasSpun = true
                luckyWheel.rotation = targetItemRotation % 360
                
                // Áp dụng phần thưởng với levelId đã nhận được
                applySpinReward(reward, levelId)
                
                showRewardDialog(reward)
                updateHeader()
            }
        })
        
        animator.start()
    }

    private fun showRewardDialog(reward: String) {
        dialogOverlay.visibility = View.VISIBLE
        tvDialogContent.text = "Bạn đã nhận được:\n$reward"
        
        val card = findViewById<View>(R.id.dialogCard)
        card.scaleX = 0.5f
        card.scaleY = 0.5f
        card.alpha = 0f
        card.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).start()
    }

    private fun applySpinReward(reward: String, levelId: Int) {
        val amount = reward.filter { it.isDigit() }.toIntOrNull() ?: 0
        
        when {
            reward.contains("50/50") ->
                preferenceManager.addSupport(PreferenceManager.SUPPORT_5050, 1)

            reward.contains("Nhân đôi cơ hội") ->
                preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_CHANCE, 1)

            reward.contains("Đáp án đúng") ->
                preferenceManager.addSupport(PreferenceManager.SUPPORT_CORRECT_ANSWER, 1)

            reward.contains("Nhân đôi điểm") ->
                preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_POINTS, 1)

            reward.contains("Xu") -> {
                if (amount > 0) preferenceManager.addXu(amount)
            }

            reward.contains("EXP") -> {
                if (amount > 0) preferenceManager.addExp(amount)
            }

            reward.contains("Điểm") -> {
                if (levelId != -1 && amount > 0) {
                    if (reward.contains("Trừ")) {
                        preferenceManager.addLevelScore(levelId, -amount)
                    } else {
                        preferenceManager.addLevelScore(levelId, amount)
                    }
                }
            }

            reward.contains("Mạng") || reward.contains("tim") -> {
                if (reward.contains("Trừ") || reward.contains("mất")) {
                    preferenceManager.useHeart()
                } else {
                    preferenceManager.addHearts(if (amount == 0) 1 else amount)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (hasSpun) setResult(RESULT_OK) else setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun updateHeader() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
    }
}
