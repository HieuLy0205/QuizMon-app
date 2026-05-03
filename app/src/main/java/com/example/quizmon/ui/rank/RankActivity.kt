package com.example.quizmon.ui.rank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.TaskHeadManager
import android.widget.ImageButton

class RankActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        preferenceManager = PreferenceManager(this)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        setupRankList()
    }

    private fun setupRankList() {
        val rvRank = findViewById<RecyclerView>(R.id.rvRank)
        rvRank.layoutManager = LinearLayoutManager(this)

        val userExp = preferenceManager.getExp()
        val userName = preferenceManager.getName()
        val userAvatar = preferenceManager.getAvatar()

        // Danh sách người chơi ảo với EXP
        val virtualPlayers = mutableListOf(
            RankItem(0, "Top1_Pro", "avatar_vip1", 5000),
            RankItem(0, "MasterQuiz", "avatar2", 4500),
            RankItem(0, "DragonKing", "avatar1", 4200),
            RankItem(0, "ShadowHunter", "avatar_vip1", 3800),
            RankItem(0, "QuizLover", "avatar2", 3500),
            RankItem(0, "NoobMaster", "avatar1", 3000),
            RankItem(0, "Zenith", "avatar2", 2800),
            RankItem(0, "Flash", "avatar1", 2500),
            RankItem(0, "Sonic", "avatar_vip1", 2000)
        )

        // Thêm người chơi hiện tại vào danh sách
        virtualPlayers.add(RankItem(0, userName, userAvatar, userExp, true))
        
        // Sắp xếp theo EXP giảm dần
        val sortedList = virtualPlayers.sortedByDescending { it.exp }
        
        // Cập nhật lại thứ hạng (rank) sau khi sắp xếp
        val finalRankList = sortedList.mapIndexed { index, item ->
            item.copy(rank = index + 1)
        }

        rvRank.adapter = RankAdapter(finalRankList)
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
