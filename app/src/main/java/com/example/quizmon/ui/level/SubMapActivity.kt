package com.example.quizmon.ui.level

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.ui.quiz.QuizActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

class SubMapActivity : AppCompatActivity() {

    private lateinit var rvSubMap: RecyclerView
    private lateinit var adapter: SubMapAdapter
    private lateinit var pbStarProgress: ProgressBar
    private lateinit var tvScore: TextView
    private lateinit var starIcons: List<ImageView>

    private val columns = 5
    private val rows = 7
    private var levelId: Int = 1
    private var mapItems = mutableListOf<SubMapItem?>()
    private var lastClickedPosition: Int = -1
    
    private var currentScore: Int = 0
    private val scorePerCorrect = 25
    private val scorePerIncorrect = 10 // Bị trừ 10 điểm khi sai

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_map)

        levelId = intent.getIntExtra("LEVEL_ID", 1)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        tvScore = findViewById(R.id.tvScore)
        pbStarProgress = findViewById(R.id.pbStarProgress)
        starIcons = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3)
        )

        rvSubMap = findViewById(R.id.rvSubMap)
        rvSubMap.layoutManager = GridLayoutManager(this, columns)

        loadMapState()
        updateUI()
        
        adapter = SubMapAdapter(mapItems) { item, position ->
            lastClickedPosition = position
            handleItemClick(item)
        }
        rvSubMap.adapter = adapter
    }

    private fun loadMapState() {
        val prefs = getSharedPreferences("QuizMonMapPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("MAP_STATE_$levelId", null)
        currentScore = prefs.getInt("SCORE_$levelId", 0)
        
        if (json != null) {
            val type = object : TypeToken<List<SubMapItem?>>() {}.type
            mapItems = Gson().fromJson(json, type)
        } else {
            generateConnectedMap()
            saveMapState()
        }
    }

    private fun saveMapState() {
        val prefs = getSharedPreferences("QuizMonMapPrefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(mapItems)
        prefs.edit().apply {
            putString("MAP_STATE_$levelId", json)
            putInt("SCORE_$levelId", currentScore)
            apply()
        }
    }

    private fun updateUI() {
        tvScore.text = "Score: $currentScore"
        
        // Giả sử 100 điểm là tối đa cho 3 sao (33, 66, 100)
        val progress = currentScore.coerceIn(0, 100)
        pbStarProgress.progress = progress

        starIcons[0].setImageResource(if (progress >= 33) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
        starIcons[1].setImageResource(if (progress >= 66) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
        starIcons[2].setImageResource(if (progress >= 100) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
    }

    private fun generateConnectedMap() {
        val categories = listOf("CNKHXH", "DiaLy", "Toan", "VanHoc", "LichSu")
        val activeCells = mutableSetOf<Pair<Int, Int>>()
        activeCells.add(Pair(columns / 2, rows / 2))

        val targetSize = Random.nextInt(12, 16)
        while (activeCells.size < targetSize) {
            val base = activeCells.random()
            val neighbors = listOf(
                Pair(base.first + 1, base.second), Pair(base.first - 1, base.second),
                Pair(base.first, base.second + 1), Pair(base.first, base.second - 1)
            ).filter { it.first in 0 until columns && it.second in 0 until rows }
            if (neighbors.isNotEmpty()) activeCells.add(neighbors.random())
        }

        val tempItems = arrayOfNulls<SubMapItem>(columns * rows)
        activeCells.forEach { (x, y) ->
            val index = y * columns + x
            tempItems[index] = SubMapItem(
                id = "item_$index",
                type = SubMapType.QUESTION,
                category = categories.random(),
                x = x,
                y = y,
                status = CompletionStatus.NOT_STARTED
            )
        }
        mapItems.clear()
        mapItems.addAll(tempItems)
    }

    private fun handleItemClick(item: SubMapItem) {
        if (item.status != CompletionStatus.NOT_STARTED) return
        val intent = Intent(this, QuizActivity::class.java)
        intent.putExtra("CATEGORY", item.category)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (lastClickedPosition != -1) {
            val item = mapItems[lastClickedPosition] ?: return
            
            if (resultCode == RESULT_OK) {
                currentScore += scorePerCorrect
                mapItems[lastClickedPosition] = item.copy(status = CompletionStatus.CORRECT)
            } else {
                currentScore = (currentScore - scorePerIncorrect).coerceAtLeast(0)
                mapItems[lastClickedPosition] = item.copy(status = CompletionStatus.INCORRECT)
            }

            adapter.notifyItemChanged(lastClickedPosition)
            saveMapState()
            updateUI()
            checkLevelCompletion()
        }
    }

    private fun checkLevelCompletion() {
        val questions = mapItems.filter { it?.type == SubMapType.QUESTION }
        val allQuestionsDone = questions.all { it?.status != CompletionStatus.NOT_STARTED }
        val atLeastOneStar = currentScore >= 33

        if (allQuestionsDone) {
            if (atLeastOneStar) {
                Toast.makeText(this, "Ải đã hoàn thành! Ải tiếp theo đã mở.", Toast.LENGTH_LONG).show()
                val mainPrefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
                val currentMax = mainPrefs.getInt("CURRENT_UNLOCKED_LEVEL", 1)
                if (levelId == currentMax) {
                    mainPrefs.edit().putInt("CURRENT_UNLOCKED_LEVEL", levelId + 1).apply()
                }
                android.os.Handler(mainLooper).postDelayed({ finish() }, 2000)
            } else {
                Toast.makeText(this, "Chưa đủ điểm (1 sao) để qua ải! Hãy thử lại.", Toast.LENGTH_LONG).show()
                // Có thể reset trạng thái INCORRECT để người dùng chơi lại các câu sai
            }
        }
    }
}
