package com.example.quizmon.ui.level

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager
import kotlin.random.Random

class TreasureActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private var hasOpened = false
    private var monsterIndex = -1
    private var levelId: Int = -1

    private lateinit var dialogOverlay: FrameLayout
    private lateinit var dialogCard: View
    private lateinit var ivRewardIcon: ImageView
    private lateinit var tvDialogTitle: TextView
    private lateinit var tvDialogContent: TextView
    private lateinit var btnDialogClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_treasure)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.treasureRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)
        levelId = intent.getIntExtra("LEVEL_ID", -1)
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
                    animateChestToCenter(chest, index == monsterIndex)
                }
            }
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            if (hasOpened) {
                val intent = Intent()
                intent.putExtra("INTERACTED", true)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
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
            val intent = Intent()
            intent.putExtra("INTERACTED", true)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

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

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(chest, "translationX", startX, targetX),
                ObjectAnimator.ofFloat(chest, "translationY", startY, targetY),
                ObjectAnimator.ofFloat(chest, "scaleX", 1f, 2.5f),
                ObjectAnimator.ofFloat(chest, "scaleY", 1f, 2.5f),
                ObjectAnimator.ofFloat(chest, "rotation", 0f, 10f, -10f, 0f)
            )
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

    private fun chestShake(chest: ImageView, isMonster: Boolean) {
        val shake = ObjectAnimator.ofFloat(chest, "rotation", 0f, 6f, -6f, 6f, 0f)
        shake.duration = 300
        shake.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                chest.animate().alpha(0f).setDuration(200).withEndAction {
                    chest.visibility = View.GONE
                    revealResultAndShowDialog(isMonster, chest)
                }.start()
            }
        })
        shake.start()
    }

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

            ivRewardIcon.setImageResource(R.drawable.darkchest)
            tvDialogTitle.text = "ÔI KHÔNG!"
            tvDialogTitle.setTextColor(Color.parseColor("#B71C1C"))
            btnDialogClose.text = "CHẤP NHẬN SỐ PHẬN"
        } else {
            val rewards = listOf("100 Xu", "200 Xu", "Cộng 1 Mạng", "50 EXP", "Phụ trợ 50/50 x1", "Phụ trợ Đáp án đúng x1")
            rawContent = rewards.random()
            // Sử dụng hàm tổng hợp Master để xử lý mượt mà
            preferenceManager.applyRewardByString(rawContent, levelId)
            fullMessage = "Chúc mừng! Bạn nhận được: $rawContent"

            ivRewardIcon.setImageResource(R.drawable.chest)
            tvDialogTitle.text = "CHÚC MỪNG!"
            tvDialogTitle.setTextColor(Color.parseColor("#2E7D32"))
            btnDialogClose.text = "NHẬN PHÚC LÀNH"
        }

        updateHeader()
        dialogOverlay.visibility = View.VISIBLE
        dialogOverlay.bringToFront()
        tvDialogContent.text = fullMessage

        dialogCard.scaleX = 0.2f
        dialogCard.scaleY = 0.2f
        dialogCard.alpha = 0f
        dialogCard.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500)
            .setInterpolator(AnticipateOvershootInterpolator()).start()
    }

    private fun animateHeartBreak(anchor: ImageView) {
        val root = findViewById<ConstraintLayout>(R.id.treasureRoot)
        val heart = ImageView(this).apply {
            setImageResource(R.drawable.tim3_shop_map)
            layoutParams = FrameLayout.LayoutParams(100, 100)
            x = anchor.x + anchor.width / 2 - 50
            y = anchor.y + anchor.height / 2 - 50
        }
        root.addView(heart)
        heart.animate().translationYBy(800f).rotation(180f).alpha(0f).setDuration(1200)
            .withEndAction { root.removeView(heart) }.start()
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

    override fun onBackPressed() {
        if (hasOpened) {
            val intent = Intent()
            intent.putExtra("INTERACTED", true)
            setResult(RESULT_OK, intent)
        }
        super.onBackPressed()
    }
}
