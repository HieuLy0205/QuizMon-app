package com.example.quizmon.ui.shop

import android.content.res.ColorStateList
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

class DailyRewardActivity : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var streakManager: StreakManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_reward)

        preferenceManager = PreferenceManager(this)
        streakManager = StreakManager(this)
        streakManager.checkAndUpdateStreak()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        setupDailyRewards()
        setupDailyTasks()
        updateHeader()
    }

    private fun updateHeader() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
    }

    private fun setupDailyRewards() {
        val currentStreak = streakManager.getCurrentStreak()
        val todayClaimed = preferenceManager.saver_va_inday("daily_reward_claimed")
        val dayInCycle = if (currentStreak == 0) 1 else ((currentStreak - 1) % 7) + 1

        val rewardViews = listOf(
            findViewById<View>(R.id.day1), findViewById<View>(R.id.day2),
            findViewById<View>(R.id.day3), findViewById<View>(R.id.day4),
            findViewById<View>(R.id.day5), findViewById<View>(R.id.day6)
        )

        val rewards = listOf(
            Triple("25 Sao", R.drawable.sao_shop_map, "sao"),
            Triple("x2 Điểm", R.drawable.ic_double_score, "support_double"),
            Triple("50/50", R.drawable.ic_fifty_fifty, "support_5050"),
            Triple("50 Xu", R.drawable.su_shop_map, "xu"),
            Triple("Gợi ý", R.drawable.ic_reveal_answer, "support_hint"),
            Triple("100 Xu", R.drawable.su_shop_map, "xu")
        )

        for (i in rewardViews.indices) {
            val cardItem = rewardViews[i]
            val dayNum = i + 1
            val tvName = cardItem.findViewById<TextView>(R.id.tvDayName)
            val tvValue = cardItem.findViewById<TextView>(R.id.tvRewardValue)
            val bg = cardItem.findViewById<LinearLayout>(R.id.layoutDayBg)
            val check = cardItem.findViewById<ImageView>(R.id.ivCheck)

            tvName.text = "Ngày $dayNum"
            tvValue.text = rewards[i].first
            cardItem.setOnClickListener(null)
            cardItem.clearAnimation()

            when {
                dayNum < dayInCycle || (dayNum == dayInCycle && todayClaimed) -> {
                    bg.setBackgroundResource(R.drawable.bg_reward_claimed)
                    bg.alpha = 1.0f
                    check.visibility = View.VISIBLE
                    tvName.text = "ĐÃ NHẬN"
                    tvName.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                }
                dayNum == dayInCycle -> {
                    bg.setBackgroundResource(R.drawable.bg_reward_active)
                    bg.alpha = 1.0f
                    check.visibility = View.GONE
                    tvName.setTextColor(ContextCompat.getColor(this, R.color.black))
                    cardItem.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                    cardItem.setOnClickListener { claimReward(dayNum, rewards[i].third) }
                }
                else -> {
                    bg.setBackgroundResource(R.drawable.bg_reward_upcoming)
                    bg.alpha = 0.5f
                    check.visibility = View.GONE
                    tvName.setTextColor(ContextCompat.getColor(this, R.color.gray))
                }
            }
        }
        
        // Ngày 7
        val layoutDay7 = findViewById<LinearLayout>(R.id.layoutDay7)
        layoutDay7.setOnClickListener(null)
        if (dayInCycle == 7) {
            if (todayClaimed) {
                layoutDay7.setBackgroundResource(R.drawable.bg_reward_claimed)
            } else {
                layoutDay7.setBackgroundResource(R.drawable.bg_reward_active)
                layoutDay7.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                layoutDay7.setOnClickListener { claimReward(7, "special") }
            }
        } else {
            layoutDay7.setBackgroundResource(R.drawable.bg_reward_upcoming)
            layoutDay7.alpha = 0.5f
        }
    }

    private fun claimReward(day: Int, type: String) {
        if (preferenceManager.saver_va_inday("daily_reward_claimed")) return

        SoundManager.playCoin()
        var gift = ""
        when (type) {
            "sao" -> { preferenceManager.addCoin(25); gift = "+25 Sao" }
            "xu" -> { val a = if(day==4) 50 else 100; preferenceManager.addXu(a); gift = "+$a Xu" }
            "support_double" -> { preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_POINTS, 2); gift = "+2 x2 Điểm" }
            "support_5050" -> { preferenceManager.addSupport(PreferenceManager.SUPPORT_5050, 2); gift = "+2 50/50" }
            "support_hint" -> { preferenceManager.addSupport(PreferenceManager.SUPPORT_CORRECT_ANSWER, 2); gift = "+2 Gợi ý" }
            "special" -> {
                preferenceManager.add_sh_Egg("1"); preferenceManager.add_sh_Egg("4"); preferenceManager.add_sh_Egg("6")
                gift = "Bộ 3 Trứng Pet!"
            }
        }
        
        preferenceManager.Xn_va_inday("daily_reward_claimed")
        setupDailyRewards()
        updateHeader()
        
        Toast.makeText(this, "Nhận thành công: $gift (Cộng vào kho đồ ở trên)", Toast.LENGTH_LONG).show()
    }

    private fun setupDailyTasks() {
        val taskDataList = listOf(
            TaskItem(R.id.task1, R.string.task_1_name, R.string.task_1_desc, "nv1", 20, "xu"),
            TaskItem(R.id.task2, R.string.task_2_name, R.string.task_2_desc, "nv5", 20, "xu"),
            TaskItem(R.id.task3, R.string.task_3_name, R.string.task_3_desc, "nv2", 20, "xu"),
            TaskItem(R.id.task4, R.string.task_4_name, R.string.task_4_desc, "nv4", 20, "xu"),
            TaskItem(R.id.task5, R.string.task_5_name, R.string.task_5_desc, "nv3", 10, "sao")
        )

        for (item in taskDataList) {
            val taskView = findViewById<View>(item.layoutId)
            val tvTitle = taskView.findViewById<TextView>(R.id.tvTaskTitle)
            val tvDesc = taskView.findViewById<TextView>(R.id.tvTaskDesc)
            val tvReward = taskView.findViewById<TextView>(R.id.tvTaskReward)
            val ivIcon = taskView.findViewById<ImageView>(R.id.ivTaskRewardIcon)
            val btn = taskView.findViewById<Button>(R.id.btnAction)

            tvTitle.text = getString(item.titleRes)
            tvDesc.text = getString(item.descRes)
            
            if (item.rewardType == "sao") {
                tvReward.text = "+ ${item.rewardAmount} Sao"
                ivIcon.setImageResource(R.drawable.sao_shop_map)
                tvReward.setTextColor(ContextCompat.getColor(this, R.color.red))
            } else {
                tvReward.text = "+ ${item.rewardAmount} Xu"
                ivIcon.setImageResource(R.drawable.su_shop_map)
                tvReward.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            }

            val isClaimed = preferenceManager.saver_va_inday(item.taskId)
            val isReady = item.taskId == "nv1" || preferenceManager.Dk_xacnhan_cq(item.taskId)

            btn.clearAnimation()
            when {
                isClaimed -> {
                    btn.text = "XONG"; btn.isEnabled = false
                    btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray))
                    taskView.alpha = 0.5f
                }
                isReady -> {
                    btn.text = "NHẬN"; btn.isEnabled = true
                    btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
                    btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                    btn.setOnClickListener {
                        SoundManager.playCoin()
                        if (item.rewardType == "sao") preferenceManager.addCoin(item.rewardAmount) 
                        else preferenceManager.addXu(item.rewardAmount)
                        
                        preferenceManager.Xn_va_inday(item.taskId)
                        setupDailyTasks()
                        updateHeader()
                        Toast.makeText(this, "Đã nhận thưởng nhiệm vụ!", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    btn.text = "LÀM NGAY"; btn.isEnabled = true
                    btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_primary))
                    btn.setOnClickListener { 
                        Toast.makeText(this, "Hãy hoàn thành: ${getString(item.descRes)}", Toast.LENGTH_SHORT).show() 
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateHeader()
        setupDailyTasks()
    }

    private data class TaskItem(
        val layoutId: Int,
        val titleRes: Int,
        val descRes: Int,
        val taskId: String,
        val rewardAmount: Int,
        val rewardType: String
    )
}
