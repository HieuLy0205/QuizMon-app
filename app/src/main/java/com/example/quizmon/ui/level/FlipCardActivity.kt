package com.example.quizmon.ui.level

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
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

class FlipCardActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private var hasSelected = false
    private var levelId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flip_card)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)
        levelId = intent.getIntExtra("LEVEL_ID", -1)
        
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            if (hasSelected) {
                val intent = Intent()
                intent.putExtra("INTERACTED", true)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
        updateHeader()

        val rewards = listOf(
            "Quẻ đại cát!\nChúc bạn ngày mới tốt lành",
            "Phụ trợ 50/50 x1",
            "Phụ trợ Nhân đôi cơ hội x1",
            "Phụ trợ Đáp án đúng x1",
            "Phụ trợ Nhân đôi điểm x1",
            "Cộng 50 Xu",
            "Cộng 100 EXP",
            "Cộng 1 Mạng",
            "Quẻ bình an:\nVạn sự hanh thông!"
        ).shuffled()

        for (i in 0 until 9) {
            val cardContainer = createCardView(rewards[i])
            gridLayout.addView(cardContainer)

            cardContainer.setOnClickListener {
                if (hasSelected) return@setOnClickListener
                hasSelected = true
                
                preferenceManager.applyRewardByString(rewards[i], levelId)
                animateFlipAndZoomToCenter(cardContainer)
            }
        }
    }

    private fun createCardView(rewardText: String): FrameLayout {
        val container = FrameLayout(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = dpToPx(95)
                height = dpToPx(130)
                setMargins(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
            }
        }

        val ivBack = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setImageResource(R.drawable.filpcard1)
            scaleType = ImageView.ScaleType.FIT_XY
        }

        val frontLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            visibility = View.GONE
            rotationY = 180f
        }

        val ivFront = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setImageResource(R.drawable.flipcard2)
            scaleType = ImageView.ScaleType.FIT_XY
        }

        val tvReward = TextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setPadding(dpToPx(10), dpToPx(20), dpToPx(10), dpToPx(10))
            text = rewardText
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#5D4037"))
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setLineSpacing(0f, 1.2f)
        }

        frontLayout.addView(ivFront)
        frontLayout.addView(tvReward)
        
        container.addView(ivBack)
        container.addView(frontLayout)

        return container
    }

    private fun animateFlipAndZoomToCenter(container: FrameLayout) {
        val ivBack = container.getChildAt(0)
        val frontLayout = container.getChildAt(1)
        val root = findViewById<ConstraintLayout>(R.id.main_root)

        val cardLoc = IntArray(2)
        container.getLocationOnScreen(cardLoc)
        val rootLoc = IntArray(2)
        root.getLocationOnScreen(rootLoc)
        
        val startX = cardLoc[0].toFloat() - rootLoc[0]
        val startY = cardLoc[1].toFloat() - rootLoc[1]

        val oldWidth = container.width
        val oldHeight = container.height
        (container.parent as ViewGroup).removeView(container)
        
        val newParams = ConstraintLayout.LayoutParams(oldWidth, oldHeight)
        container.layoutParams = newParams
        container.x = startX
        container.y = startY
        root.addView(container)

        val targetX = (root.width - oldWidth) / 2f
        val targetY = (root.height - oldHeight) / 2f

        container.pivotX = oldWidth / 2f
        container.pivotY = oldHeight / 2f

        container.animate()
            .x(targetX)
            .y(targetY)
            .scaleX(3.5f)
            .scaleY(3.5f)
            .rotationY(180f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                updateHeader()
                android.os.Handler(mainLooper).postDelayed({
                    if (!isFinishing) {
                        val intent = Intent()
                        intent.putExtra("INTERACTED", true)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }, 2500)
            }
            .start()

        container.postDelayed({
            ivBack.visibility = View.GONE
            frontLayout.visibility = View.VISIBLE
        }, 400)
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

    override fun onBackPressed() {
        if (hasSelected) {
            val intent = Intent()
            intent.putExtra("INTERACTED", true)
            setResult(RESULT_OK, intent)
        }
        super.onBackPressed()
    }
}
