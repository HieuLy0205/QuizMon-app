package com.example.quizmon.ui.shop

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.StreakManager
import com.example.quizmon.utils.TaskHeadManager

class shop_phobien : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var streakManager: StreakManager
    private lateinit var btnBack: ImageButton
    private lateinit var btnDoubleReward: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_phobien)

        preferenceManager = PreferenceManager(this)
        streakManager = StreakManager(this)
        
        // Cập nhật chuỗi đăng nhập
        streakManager.checkAndUpdateStreak()

        btnBack = findViewById(R.id.btnBack)
        btnDoubleReward = findViewById(R.id.btnDoubleReward)

        btnBack.setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        setupDailyRewards()
        setupDailyTasks()
        
        btnDoubleReward.setOnClickListener {
            SoundManager.playClick()
            Toast.makeText(this, "Xem quảng cáo nhân đôi quà đang bảo trì!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDailyRewards() {
        val currentStreak = streakManager.getCurrentStreak()
        val todayClaimed = preferenceManager.saver_va_inday("daily_reward_claimed")
        
        // Xác định ngày trong vòng lặp 7 ngày
        val dayInCycle = if (currentStreak == 0) 1 else ((currentStreak - 1) % 7) + 1

        val rewardViews = listOf(
            findViewById<View>(R.id.day1), findViewById<View>(R.id.day2),
            findViewById<View>(R.id.day3), findViewById<View>(R.id.day4),
            findViewById<View>(R.id.day5), findViewById<View>(R.id.day6)
        )

        // Cấu hình quà tặng theo yêu cầu: [Tên, Icon, Loại quà]
        val rewards = listOf(
            Triple("25 Sao ước", R.drawable.sao_shop_map, "sao"),
            Triple("Nhân đôi x2", R.drawable.ic_double_score, "support_double"),
            Triple("50/50 x2", R.drawable.ic_fifty_fifty, "support_5050"),
            Triple("50 Xu cỏ", R.drawable.su_shop_map, "xu"),
            Triple("Gợi ý x2", R.drawable.ic_reveal_answer, "support_hint"),
            Triple("100 Xu cỏ", R.drawable.su_shop_map, "xu")
        )

        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)

        for (i in rewardViews.indices) {
            val view = rewardViews[i]
            val dayNum = i + 1
            view.findViewById<TextView>(R.id.tvDayName).text = "Ngày $dayNum"
            view.findViewById<TextView>(R.id.tvRewardValue).text = rewards[i].first
            view.findViewById<ImageView>(R.id.ivRewardIcon).setImageResource(rewards[i].second)

            val bg = view.findViewById<LinearLayout>(R.id.layoutDayBg)
            val check = view.findViewById<ImageView>(R.id.ivCheck)

            when {
                dayNum < dayInCycle || (dayNum == dayInCycle && todayClaimed) -> {
                    // Đã nhận quà
                    bg.setBackgroundResource(R.drawable.bg_stats_bar)
                    bg.alpha = 0.6f
                    check.visibility = View.VISIBLE
                    view.setOnClickListener(null)
                }
                dayNum == dayInCycle -> {
                    // Ngày hiện tại chưa nhận - Chạy Animation Rung
                    bg.setBackgroundResource(R.drawable.bg_card_overlay_selector)
                    view.startAnimation(shakeAnim)
                    view.setOnClickListener { claimReward(dayNum, rewards[i].third) }
                }
                else -> {
                    // Các ngày tiếp theo
                    bg.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                    bg.alpha = 0.3f
                }
            }
        }
        
        // Ngày 7 Đặc biệt
        val layoutDay7 = findViewById<View>(R.id.layoutDay7)
        if (dayInCycle == 7) {
            if (todayClaimed) {
                layoutDay7.alpha = 0.6f
                layoutDay7.setOnClickListener(null)
            } else {
                layoutDay7.startAnimation(shakeAnim)
                layoutDay7.setOnClickListener { claimReward(7, "special") }
            }
        } else {
            layoutDay7.alpha = 0.3f
        }
    }

    private fun claimReward(day: Int, type: String) {
        if (preferenceManager.saver_va_inday("daily_reward_claimed")) return

        SoundManager.playCoin()
        when (type) {
            "sao" -> preferenceManager.addCoin(25)
            "xu" -> preferenceManager.addXu(if(day == 4) 50 else 100)
            "support_double" -> preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_POINTS, 2)
            "support_5050" -> preferenceManager.addSupport(PreferenceManager.SUPPORT_5050, 2)
            "support_hint" -> preferenceManager.addSupport(PreferenceManager.SUPPORT_CORRECT_ANSWER, 2)
            "special" -> {
                preferenceManager.add_sh_Egg("1")
                preferenceManager.add_sh_Egg("4")
                preferenceManager.add_sh_Egg("6")
                Toast.makeText(this, "WOW! Bạn nhận được bộ 3 Trứng Pet cực hiếm!", Toast.LENGTH_LONG).show()
            }
        }
        
        preferenceManager.Xn_va_inday("daily_reward_claimed")
        
        // Animation chúc mừng khi nhấn nhận
        val claimAnim = AnimationUtils.loadAnimation(this, R.anim.pet_bounce)
        findViewById<View>(R.id.tvTitle).startAnimation(claimAnim)
        
        Toast.makeText(this, "Điểm danh ngày $day thành công!", Toast.LENGTH_SHORT).show()
        setupDailyRewards()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
    }

    private fun setupDailyTasks() {
        val tasks = listOf(
            Triple(R.id.task1, "Đăng nhập hôm nay", "nv1"),
            Triple(R.id.task2, "Trả lời đúng 1 câu", "nv2"),
            Triple(R.id.task3, "Vượt qua 1 cấp độ", "nv3"),
            Triple(R.id.task4, "Dùng vòng quay may mắn", "nv4"),
            Triple(R.id.task5, "Thắng 3 trận PVP (Cực khó)", "nv5")
        )

        for (i in tasks.indices) {
            val taskView = findViewById<View>(tasks[i].first)
            val taskId = tasks[i].third
            val title = tasks[i].second
            val btn = taskView.findViewById<Button>(R.id.btnAction)
            val tvReward = taskView.findViewById<TextView>(R.id.tvTaskReward)
            val ivIcon = taskView.findViewById<ImageView>(R.id.ivTaskRewardIcon)

            taskView.findViewById<TextView>(R.id.tvTaskTitle).text = title

            // NV khó nhất thưởng Sao ước, các NV khác thưởng Xu cỏ
            if (taskId == "nv5") {
                tvReward.text = "+ 10 Sao ước"
                tvReward.setTextColor(ContextCompat.getColor(this, R.color.red))
                ivIcon.setImageResource(R.drawable.sao_shop_map)
            } else {
                tvReward.text = "+ 20 Xu cỏ"
                ivIcon.setImageResource(R.drawable.su_shop_map)
            }
            
            if (preferenceManager.saver_va_inday(taskId)) {
                btn.isEnabled = false
                btn.text = "Đã xong"
                btn.alpha = 0.5f
            } else {
                btn.setOnClickListener {
                    if (taskId == "nv1" || preferenceManager.Dk_xacnhan_cq(taskId)) {
                        SoundManager.playCoin()
                        if (taskId == "nv5") preferenceManager.addCoin(10) else preferenceManager.addXu(20)
                        preferenceManager.Xn_va_inday(taskId)
                        btn.isEnabled = false
                        btn.text = "Đã xong"
                        Toast.makeText(this, "Nhiệm vụ hoàn tất!", Toast.LENGTH_SHORT).show()
                        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
                    } else {
                        SoundManager.playWrong()
                        Toast.makeText(this, "Bạn chưa hoàn thành yêu cầu này!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
    }
}
