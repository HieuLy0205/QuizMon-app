package com.example.quizmon.ui.level

import android.animation.Animator
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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var ivRewardIcon: ImageView
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

        monsterIndex = Random.nextInt(5)

        chests.forEachIndexed { index, chest ->
            chest.setOnClickListener {
                if (!hasOpened) {
                    hasOpened = true
                    setResult(RESULT_OK)
                    animateChestToCenter(chest, index == monsterIndex)
                }
            }
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
        updateHeader()
    }

    private fun initViews() {
        dialogOverlay = findViewById(R.id.dialogOverlay)
        dialogCard = findViewById(R.id.dialogCard)
        ivRewardIcon = findViewById(R.id.ivRewardIcon)
        tvDialogTitle = findViewById(R.id.tvDialogTitle)
        tvDialogContent = findViewById(R.id.tvDialogContent)
        btnDialogClose = findViewById(R.id.btnDialogClose)

        btnDialogClose.setOnClickListener {
            dialogOverlay.visibility = View.GONE
            finish()
        }
    }

    // ===================== RƯƠNG BAY =====================
    private fun animateChestToCenter(chest: ImageView, isMonster: Boolean) {

        val root = findViewById<ConstraintLayout>(R.id.treasureRoot)

        val loc = IntArray(2)
        chest.getLocationInWindow(loc)

        val rootLoc = IntArray(2)
        root.getLocationInWindow(rootLoc)

        val startX = loc[0].toFloat() - rootLoc[0]
        val startY = loc[1].toFloat() - rootLoc[1]

        val w = chest.width
        val h = chest.height

        (chest.parent as? ViewGroup)?.removeView(chest)

        chest.layoutParams = ConstraintLayout.LayoutParams(w, h)
        chest.translationX = startX
        chest.translationY = startY
        root.addView(chest)

        val targetX = (root.width - w) / 2f
        val targetY = (root.height - h) / 2f

        chest.cameraDistance = 8000 * resources.displayMetrics.density

        val moveX = ObjectAnimator.ofFloat(chest, "translationX", startX, targetX)
        val moveY = ObjectAnimator.ofFloat(chest, "translationY", startY, targetY)
        val scaleX = ObjectAnimator.ofFloat(chest, "scaleX", 1f, 2.5f)
        val scaleY = ObjectAnimator.ofFloat(chest, "scaleY", 1f, 2.5f)
        val rotate = ObjectAnimator.ofFloat(chest, "rotation", 0f, 10f, -10f, 0f)

        AnimatorSet().apply {
            playTogether(moveX, moveY, scaleX, scaleY, rotate)
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()

            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    chestShake(chest, isMonster)
                }
            })
            start()
        }
    }

    // ===================== SHAKE =====================
    private fun chestShake(chest: ImageView, isMonster: Boolean) {

        val shake = ObjectAnimator.ofFloat(chest, "rotation", 0f, 6f, -6f, 6f, 0f)
        shake.duration = 300

        shake.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {

                chest.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        chest.visibility = View.GONE
                        revealResultAndShowDialog(isMonster, chest)
                    }
                    .start()
            }
        })

        shake.start()
    }

    // ===================== GỘP LOGIC XỬ LÝ VÀ HIỂN THỊ =====================
    private fun revealResultAndShowDialog(isMonster: Boolean, anchor: ImageView) {
        val fullMessage: String
        val rawContent: String

        if (isMonster) {
            preferenceManager.useHeart()

            val monsterMessages = listOf(
                getString(R.string.monster_message_1),
                getString(R.string.monster_message_2),
                getString(R.string.monster_message_3)
            )
            rawContent = monsterMessages.random()
            fullMessage = "Không xong rồi: $rawContent"
            animateHeartBreak(anchor)

            // UI cho Quái vật
            ivRewardIcon.setImageResource(R.drawable.darkchest)
            tvDialogTitle.text = "ÔI KHÔNG!"
            tvDialogTitle.setTextColor(Color.parseColor("#B71C1C"))
            btnDialogClose.text = "CHẤP NHẬN SỐ PHẬN"

        } else {
            val rewards = listOf(
                "100 Xu",
                "200 Xu",
                "1 Mạng",
                "50 EXP",
                "Phụ trợ 50/50 x1",
                "Phụ trợ Đáp án đúng x1",
                "Phụ trợ Nhân đôi điểm x1",
                "Phụ trợ Nhân đôi cơ hội x1"
            )

            rawContent = rewards.random()
            applyReward(rawContent)
            fullMessage = "Chúc mừng! Bạn nhận được: $rawContent"

            // UI cho Thưởng
            ivRewardIcon.setImageResource(R.drawable.chest)
            tvDialogTitle.text = "CHÚC MỪNG!"
            tvDialogTitle.setTextColor(Color.parseColor("#2E7D32"))
            btnDialogClose.text = "NHẬN PHÚC LÀNH"
        }

        updateHeader()

        // HIỂN THỊ DIALOG
        dialogOverlay.visibility = View.VISIBLE
        dialogOverlay.bringToFront()
        tvDialogContent.text = fullMessage

        // Hiệu ứng Pop-up Dialog
        dialogCard.scaleX = 0.2f
        dialogCard.scaleY = 0.2f
        dialogCard.alpha = 0f

        dialogCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(AnticipateOvershootInterpolator())
            .start()
    }

    // ===================== HEART BREAK =====================
    private fun animateHeartBreak(anchor: ImageView) {

        val root = findViewById<ConstraintLayout>(R.id.treasureRoot)

        val heart = ImageView(this).apply {
            setImageResource(R.drawable.tim3_shop_map)
            layoutParams = FrameLayout.LayoutParams(100, 100)
        }

        root.addView(heart)

        // Căn giữa trái tim vào rương
        heart.x = anchor.x + anchor.width / 2 - 50
        heart.y = anchor.y + anchor.height / 2 - 50

        heart.animate()
            .translationYBy(800f)
            .rotation(180f)
            .alpha(0f)
            .setDuration(1200)
            .withEndAction {
                root.removeView(heart)
            }
            .start()
    }

    private fun applyReward(reward: String) {
        when {
            reward.contains("100") -> preferenceManager.addXu(100)
            reward.contains("200") -> preferenceManager.addXu(200)
            reward.contains("Mạng") -> preferenceManager.addHearts(1)
            reward.contains("EXP") -> preferenceManager.addExp(50)
            reward.contains("50/50") -> preferenceManager.addSupport(PreferenceManager.SUPPORT_5050, 1)
            reward.contains("Đáp án đúng") -> preferenceManager.addSupport(PreferenceManager.SUPPORT_CORRECT_ANSWER, 1)
            reward.contains("Nhân đôi điểm") -> preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_POINTS, 1)
            reward.contains("Nhân đôi cơ hội") -> preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_CHANCE, 1)
        }
    }

    private fun updateHeader() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
}