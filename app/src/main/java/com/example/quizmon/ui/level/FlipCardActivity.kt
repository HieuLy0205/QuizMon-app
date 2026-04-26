package com.example.quizmon.ui.level

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.quizmon.R
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager

class FlipCardActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private var hasSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_card)

        preferenceManager = PreferenceManager(this)
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }
        updateHeader()

        //Thêm dấu xuống dòng \n để chữ hiển thị đẹp hơn
        val rewards = listOf(
            "Quẻ đại cát!\nChúc bạn ngày mới tốt lành",
            "Nhận 1 phụ trợ\n50/50",
            "Nhận 1 phụ trợ\nNhân đôi cơ hội",
            "Nhận 1 phụ trợ\nđáp án đúng",
            "Nhận 1 phụ trợ\nBỏ qua câu hỏi",
            "Nhận 50 Xu\nmay mắn",
            "Nhận 100 EXP\nkinh nghiệm",
            "Nhận 1 Mạng\nhồi sinh",
            "Quẻ bình an:\nVạn sự hanh thông!"
        ).shuffled()

        for (i in 0 until 9) {
            val cardContainer = createCardView(rewards[i])
            gridLayout.addView(cardContainer)

            cardContainer.setOnClickListener {
                if (hasSelected) return@setOnClickListener
                hasSelected = true
                
                setResult(RESULT_OK)
                handleRewardLogic(rewards[i])
                
                //Gọi hàm lật và phóng to ra giữa màn hình
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

        // Mặt sau
        val ivBack = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setImageResource(R.drawable.filpcard1)
            scaleType = ImageView.ScaleType.FIT_XY
        }

        // Mặt trước
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
            setLineSpacing(0f, 1.2f) // Giãn dòng cho dễ đọc
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

        // 1. Lấy vị trí thẻ hiện tại so với màn hình trước khi tách khỏi Grid
        val cardLoc = IntArray(2)
        container.getLocationOnScreen(cardLoc)
        val rootLoc = IntArray(2)
        root.getLocationOnScreen(rootLoc)
        
        val startX = cardLoc[0].toFloat() - rootLoc[0]
        val startY = cardLoc[1].toFloat() - rootLoc[1]

        // 2. Tách thẻ ra khỏi GridLayout và đưa lên lớp cao nhất (main_root) 
        // để không bị cắt biên (Clipping)
        val oldWidth = container.width
        val oldHeight = container.height
        (container.parent as ViewGroup).removeView(container)
        
        val newParams = ConstraintLayout.LayoutParams(oldWidth, oldHeight)
        container.layoutParams = newParams
        container.x = startX
        container.y = startY
        root.addView(container)

        // 3. Tính toán vị trí tâm màn hình
        val targetX = (root.width - oldWidth) / 2f
        val targetY = (root.height - oldHeight) / 2f

        // Đảm bảo tâm phóng to nằm giữa thẻ
        container.pivotX = oldWidth / 2f
        container.pivotY = oldHeight / 2f

        // 4. Thực hiện hoạt ảnh bay ra giữa + phóng to + lật mặt
        container.animate()
            .x(targetX)
            .y(targetY)
            .scaleX(3.5f) // Phóng to cực đại
            .scaleY(3.5f)
            .rotationY(180f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                updateHeader()
                android.os.Handler(mainLooper).postDelayed({
                    if (!isFinishing) finish()
                }, 2500)
            }
            .start()

        // Đổi ảnh mặt trước khi thẻ xoay đến "cạnh" (90 độ)
        container.postDelayed({
            ivBack.visibility = View.GONE
            frontLayout.visibility = View.VISIBLE
        }, 400)
    }

    private fun handleRewardLogic(reward: String) {
        when {
            reward.contains("50 Xu") -> preferenceManager.addXu(50)
            reward.contains("100 EXP") -> preferenceManager.addExp(100)
            reward.contains("1 Mạng") -> preferenceManager.saveHearts((preferenceManager.getHearts() + 1).coerceAtMost(5))
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
