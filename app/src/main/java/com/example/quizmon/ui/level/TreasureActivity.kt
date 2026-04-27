package com.example.quizmon.ui.level

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager
import kotlin.random.Random

class TreasureActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private var hasOpened = false
    private var monsterIndex = -1

    private lateinit var dialogOverlay: FrameLayout
    private lateinit var dialogCard: View
    private lateinit var ivDialogChest: ImageView
    private lateinit var tvDialogTitle: TextView
    private lateinit var tvDialogContent: TextView
    private lateinit var btnDialogClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treasure)

        preferenceManager = PreferenceManager(this)
        initViews()
        
        val chests = listOf(
            findViewById<ImageView>(R.id.chest1),
            findViewById<ImageView>(R.id.chest2),
            findViewById<ImageView>(R.id.chest3),
            findViewById<ImageView>(R.id.chest4),
            findViewById<ImageView>(R.id.chest5)
        )

        // Chỉ định ngẫu nhiên 1 rương là quái thú (monster)
        monsterIndex = Random.nextInt(5)

        chests.forEachIndexed { index, chest ->
            chest.setOnClickListener {
                if (!hasOpened) {
                    hasOpened = true
                    setResult(RESULT_OK)
                    handleChestOpening(chest, index == monsterIndex)
                }
            }
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
        
        updateHeader()
    }

    private fun initViews() {
        dialogOverlay = findViewById(R.id.dialogOverlay)
        dialogCard = findViewById(R.id.dialogCard)
        ivDialogChest = findViewById(R.id.ivDialogChest)
        tvDialogTitle = findViewById(R.id.tvDialogTitle)
        tvDialogContent = findViewById(R.id.tvDialogContent)
        btnDialogClose = findViewById(R.id.btnDialogClose)

        btnDialogClose.setOnClickListener {
            hideRewardDialog()
            finish()
        }
    }

    private fun handleChestOpening(chest: ImageView, isMonster: Boolean) {
        // Hoạt ảnh rung rinh
        val shake = ObjectAnimator.ofFloat(chest, "translationX", 0f, 15f, -15f, 15f, -15f, 0f).apply {
            duration = 500
        }

        val fullAnim = AnimatorSet().apply {
            play(shake)
            interpolator = AccelerateDecelerateInterpolator()
        }

        fullAnim.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                if (isMonster) {
                    chest.setImageResource(R.drawable.darkchest)
                    //Hình phạt mới: Trừ 1 tim
                    preferenceManager.useHeart()
                    showRewardDialog(isMonster = true, content = "Rương quái thú!\nBạn bị trừ 1 mạng hồi sinh!")
                    //Thực hiện hoạt ảnh trái tim vỡ rơi xuống
                    animateHeartBreak(chest)
                } else {
                    val rewards = listOf("50 Xu", "100 Xu", "1 Mạng", "20 EXP")
                    val reward = rewards.random()
                    applyReward(reward)
                    showRewardDialog(isMonster = false, content = "Tuyệt vời!\nBạn nhận được $reward")
                }
                updateHeader()
            }
        })
        fullAnim.start()
    }

    private fun animateHeartBreak(anchorView: ImageView) {
        // Tạo một ImageView trái tim tạm thời
        val heart = ImageView(this).apply {
            setImageResource(R.drawable.tim3_shop_map)
            layoutParams = FrameLayout.LayoutParams(dpToPx(40), dpToPx(40))
        }
        
        val root = findViewById<FrameLayout>(R.id.treasureRoot)
        root.addView(heart)

        // Lấy vị trí của rương để làm điểm bắt đầu cho tim
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val rootLoc = IntArray(2)
        root.getLocationOnScreen(rootLoc)

        heart.x = location[0].toFloat() - rootLoc[0] + (anchorView.width / 2) - dpToPx(20)
        heart.y = location[1].toFloat() - rootLoc[1]

        // Hoạt ảnh: Rơi xuống + Xoay + Mờ dần
        val fall = ObjectAnimator.ofFloat(heart, "translationY", heart.y, heart.y + 600f)
        val rotate = ObjectAnimator.ofFloat(heart, "rotation", 0f, 60f)
        val fade = ObjectAnimator.ofFloat(heart, "alpha", 1f, 0f)

        AnimatorSet().apply {
            playTogether(fall, rotate, fade)
            duration = 1500
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    root.removeView(heart)
                }
            })
            start()
        }
    }

    private fun showRewardDialog(isMonster: Boolean, content: String) {
        dialogOverlay.visibility = View.VISIBLE
        
        if (isMonster) {
            ivDialogChest.setImageResource(R.drawable.darkchest)
            tvDialogTitle.text = "ÔI KHÔNG!"
            tvDialogTitle.setTextColor(Color.RED)
            btnDialogClose.text = "THOÁT"
        } else {
            ivDialogChest.setImageResource(R.drawable.chest)
            tvDialogTitle.text = "CHÚC MỪNG!"
            tvDialogTitle.setTextColor(Color.parseColor("#2E7D32"))
            btnDialogClose.text = "TUYỆT VỜI"
        }
        
        tvDialogContent.text = content

        // Hoạt ảnh Dialog bay lên và phóng to
        dialogCard.scaleX = 0.5f
        dialogCard.scaleY = 0.5f
        dialogCard.alpha = 0f
        
        dialogCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(AnticipateOvershootInterpolator())
            .start()
    }

    private fun hideRewardDialog() {
        dialogOverlay.visibility = View.GONE
    }

    private fun applyReward(reward: String) {
        when {
            reward.contains("Xu") -> {
                val amount = reward.split(" ")[0].toIntOrNull() ?: 50
                preferenceManager.addXu(amount)
            }
            reward.contains("Mạng") -> {
                preferenceManager.addHearts(1)
            }
            reward.contains("EXP") -> {
                val amount = reward.split(" ")[0].toIntOrNull() ?: 20
                preferenceManager.addExp(amount)
            }
        }
    }

    private fun updateHeader() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
}
