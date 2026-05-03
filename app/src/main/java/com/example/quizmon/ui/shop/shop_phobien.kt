package com.example.quizmon.ui.shop

import android.os.Bundle
import android.view.View
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
            Toast.makeText(this, "Tính năng xem quảng cáo đang được phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDailyRewards() {
        val currentStreak = streakManager.getCurrentStreak()
        val todayClaimed = preferenceManager.saver_va_inday("daily_reward_claimed")
        
        // Xác định ngày hiện tại trong chuỗi 7 ngày (1-7)
        val dayInCycle = ((currentStreak - 1) % 7) + 1

        val rewardViews = listOf(
            findViewById<View>(R.id.day1), findViewById<View>(R.id.day2),
            findViewById<View>(R.id.day3), findViewById<View>(R.id.day4),
            findViewById<View>(R.id.day5), findViewById<View>(R.id.day6)
        )

        val rewards = listOf(
            "x25 Xu" to R.drawable.sao_shop_map,
            "Nhân đôi x5" to R.drawable.sao_shop_map,
            "Điểm x100" to R.drawable.sao_shop_map,
            "x50 Xu" to R.drawable.sao_shop_map,
            "Bỏ qua x5" to R.drawable.sao_shop_map,
            "x75 Xu" to R.drawable.sao_shop_map
        )

        for (i in rewardViews.indices) {
            val view = rewardViews[i]
            val dayNum = i + 1
            view.findViewById<TextView>(R.id.tvDayName).text = "Ngày $dayNum"
            view.findViewById<TextView>(R.id.tvRewardValue).text = rewards[i].first
            view.findViewById<ImageView>(R.id.ivRewardIcon).setImageResource(rewards[i].second)

            val bg = view.findViewById<LinearLayout>(R.id.layoutDayBg)
            val check = view.findViewById<ImageView>(R.id.ivCheck)

            if (dayNum < dayInCycle || (dayNum == dayInCycle && todayClaimed)) {
                // Đã nhận
                bg.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                check.visibility = View.VISIBLE
            } else if (dayNum == dayInCycle) {
                // Sẵn sàng nhận
                bg.setBackgroundResource(R.drawable.bg_stats_bar) // Highlight màu vàng/cam
                view.setOnClickListener { claimReward(dayNum) }
            }
        }
        
        // Xử lý ngày 7 đặc biệt
        val layoutDay7 = findViewById<View>(R.id.layoutDay7)
        if (dayInCycle == 7 && !todayClaimed) {
            layoutDay7.setOnClickListener { claimReward(7) }
        }
    }

    private fun claimReward(day: Int) {
        if (preferenceManager.saver_va_inday("daily_reward_claimed")) return

        SoundManager.playCoin()
        when (day) {
            1 -> preferenceManager.addCoin(25)
            2 -> preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_CHANCE, 5)
            3 -> preferenceManager.addLevelScore(1, 100) // Ví dụ cộng vào ải 1
            4 -> preferenceManager.addCoin(50)
            5 -> preferenceManager.addSupport(PreferenceManager.SUPPORT_CORRECT_ANSWER, 5) // Dùng tạm cho Bỏ qua
            6 -> preferenceManager.addCoin(75)
            7 -> {
                preferenceManager.add_sh_Egg("1")
                preferenceManager.add_sh_Egg("4")
                preferenceManager.add_sh_Egg("6")
            }
        }
        
        preferenceManager.Xn_va_inday("daily_reward_claimed")
        Toast.makeText(this, "Đã nhận thưởng ngày $day!", Toast.LENGTH_SHORT).show()
        setupDailyRewards() // Cập nhật lại UI
    }

    private fun setupDailyTasks() {
        val tasks = listOf(
            Triple(R.id.task1, "Đăng nhập nhận thưởng", "nv1"),
            Triple(R.id.task2, "Hoàn thành 1 ải nhỏ", "nv2"),
            Triple(R.id.task3, "Nạp xu vào tài khoản", "nv3"),
            Triple(R.id.task4, "Mua trứng trong PVP", "nv4"),
            Triple(R.id.task5, "Trả lời đúng 1 câu hỏi", "nv5")
        )

        val taskRewards = listOf(10, 20, 20, 10, 5)

        for (i in tasks.indices) {
            val taskView = findViewById<View>(tasks[i].first)
            val taskId = tasks[i].third
            val title = tasks[i].second
            val rewardAmount = taskRewards[i]

            taskView.findViewById<TextView>(R.id.tvTaskTitle).text = title
            taskView.findViewById<TextView>(R.id.tvTaskReward).text = "+ $rewardAmount Xu"
            
            val btn = taskView.findViewById<Button>(R.id.btnAction)

            if (preferenceManager.saver_va_inday(taskId)) {
                btn.isEnabled = false
                btn.text = "Đã xong"
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray))
            } else {
                btn.setOnClickListener {
                    if (taskId == "nv1" || preferenceManager.Dk_xacnhan_cq(taskId)) {
                        SoundManager.playCoin()
                        preferenceManager.addCoin(rewardAmount)
                        preferenceManager.Xn_va_inday(taskId)
                        btn.isEnabled = false
                        btn.text = "Đã xong"
                        Toast.makeText(this, "Hoàn thành nhiệm vụ!", Toast.LENGTH_SHORT).show()
                    } else {
                        SoundManager.playWrong()
                        val msg = when(taskId) {
                            "nv2" -> "Chưa xong ải nào!"
                            "nv3" -> "Hãy nạp xu trước!"
                            "nv4" -> "Hãy mua trứng trong shop PVP!"
                            "nv5" -> "Hãy trả lời đúng 1 câu hỏi!"
                            else -> "Chưa hoàn thành yêu cầu!"
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
        SoundManager.playMusic(this, R.raw.background)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
        SoundManager.pauseMusic()
    }
}
