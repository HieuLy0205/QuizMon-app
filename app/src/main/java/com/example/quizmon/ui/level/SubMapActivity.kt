package com.example.quizmon.ui.level

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.ui.quiz.QuizActivity
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SubMapActivity : AppCompatActivity() {

    private lateinit var rvSubMap: RecyclerView
    private lateinit var adapter: SubMapAdapter
    private lateinit var pbStarProgress: ProgressBar
    
    private lateinit var tvCurrentStageScore: TextView
    private lateinit var starIcons: List<ImageView>

    private val columns = 7
    private val rows = 9
    private var levelId: Int = 1
    private var mapItems = mutableListOf<SubMapItem?>()
    private var lastClickedPosition: Int = -1

    private var currentScore: Int = 0
    private val scorePerCorrect = 30
    private val scorePerIncorrect = 10
    
    private val star1Score = 100
    private val star2Score = 200
    private val star3Score = 300
    // Cập nhật maxPossibleScore dựa trên tổng số câu hỏi thực tế có thể có
    // Ở đây ta dùng 300 (điểm 3 sao) làm mốc 100% của thanh Progress
    private val maxProgressScore = 300

    private lateinit var preferenceManager: PreferenceManager
    
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateUI()
            updateHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sub_map)

        if (savedInstanceState != null) {
            lastClickedPosition = savedInstanceState.getInt("LAST_CLICKED_POSITION", -1)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sub_map_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)
        levelId = intent.getIntExtra("LEVEL_ID", 1)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        tvCurrentStageScore = findViewById(R.id.tvCurrentStageScore)
        
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
            if (item.status == CompletionStatus.NOT_STARTED) {
                lastClickedPosition = position
                handleItemClick(item)
            } else {
                Toast.makeText(this, "Ô này đã hoàn thành!", Toast.LENGTH_SHORT).show()
            }
        }
        rvSubMap.adapter = adapter
    }

    private fun loadMapState() {
        val prefs = getSharedPreferences("QuizMonMapPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("MAP_STATE_$levelId", null)
        currentScore = prefs.getInt("SCORE_$levelId", 0)

        if (json != null) {
            try {
                val type = object : TypeToken<List<SubMapItem?>>() {}.type
                val loadedItems: List<SubMapItem?> = Gson().fromJson(json, type)
                mapItems.clear()
                mapItems.addAll(loadedItems)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (mapItems.isEmpty()) {
            generateMapWithShape()
            saveMapState()
        }
        
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun saveMapState() {
        val prefs = getSharedPreferences("QuizMonMapPrefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(mapItems)
        prefs.edit().apply {
            putString("MAP_STATE_$levelId", json)
            putInt("SCORE_$levelId", currentScore)
        }.commit() 
    }

    private fun updateUI() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
        
        tvCurrentStageScore.text = currentScore.toString()
        
        when {
            currentScore > 0 -> tvCurrentStageScore.setTextColor(Color.parseColor("#4CAF50")) 
            currentScore < 0 -> tvCurrentStageScore.setTextColor(Color.parseColor("#F44336")) 
            else -> tvCurrentStageScore.setTextColor(Color.BLACK)
        }

        // Cập nhật logic tính % thanh Progress
        // Nếu đạt mốc 300 điểm (3 sao) thì thanh Progress phải đầy (100%)
        val progressPercent = if (currentScore > 0) {
            ((currentScore.toFloat() / maxProgressScore) * 100).toInt().coerceIn(0, 100)
        } else 0
        pbStarProgress.progress = progressPercent

        starIcons[0].setImageResource(if (currentScore >= star1Score) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
        starIcons[1].setImageResource(if (currentScore >= star2Score) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
        starIcons[2].setImageResource(if (currentScore >= star3Score) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
    }

    private fun generateMapWithShape() {
        val allCategories = mutableListOf(
            "AmNhac", "ChoiChu", "CNXHKH", "DiaLy", "DoVui",
            "TiengAnh", "HoaHoc", "KienThucChung", "KinhTeChinhTri",
            "LichSu", "TinHoc", "TuTuongHCM", "VanHoc", "VatLy"
        ).shuffled()

        val robotShape = listOf(
            Pair(3,0), Pair(3,1), Pair(2,2), Pair(3,2), Pair(4,2),
            Pair(1,3), Pair(2,3), Pair(3,3), Pair(4,3), Pair(5,3),
            Pair(2,4), Pair(3,4), Pair(4,4), Pair(2,5), Pair(4,5), Pair(2,6), Pair(4,6)
        )
        val flowerShape = listOf(
            Pair(3,3), Pair(3,2), Pair(4,2), Pair(4,3), Pair(4,4), Pair(3,4), Pair(2,4), Pair(2,3), Pair(2,2),
            Pair(3,5), Pair(3,6), Pair(3,7), Pair(2,6), Pair(4,6), Pair(1,1), Pair(5,1), Pair(1,5)
        )
        val towerShape = listOf(
            Pair(3,1), Pair(3,2), Pair(2,2), Pair(4,2), Pair(3,3), Pair(1,3), Pair(5,3),
            Pair(3,4), Pair(2,4), Pair(4,4), Pair(3,5), Pair(0,5), Pair(6,5), Pair(3,6), Pair(1,6), Pair(5,6), Pair(3,7)
        )

        val shapeCoords = when (levelId % 3) {
            1 -> robotShape
            2 -> flowerShape
            else -> towerShape
        }

        val tempItems = arrayOfNulls<SubMapItem>(columns * rows)
        val shuffledCoords = shapeCoords.shuffled()

        for (i in 0 until allCategories.size.coerceAtMost(shuffledCoords.size)) {
            val (x, y) = shuffledCoords[i]
            tempItems[y * columns + x] = SubMapItem(
                id = "q_$i", type = SubMapType.QUESTION, category = allCategories[i], x = x, y = y
            )
        }

        val specialTypes = listOf(
            SubMapType.SPIN_WHEEL to "wheel",
            SubMapType.TREASURE to "chest",
            SubMapType.FLIP_CARD to "draw"
        )

        for (i in 0 until specialTypes.size) {
            val coordIndex = allCategories.size + i
            if (coordIndex < shuffledCoords.size) {
                val (x, y) = shuffledCoords[coordIndex]
                val (type, id) = specialTypes[i]
                tempItems[y * columns + x] = SubMapItem(id = id, type = type, x = x, y = y)
            }
        }

        mapItems.clear()
        mapItems.addAll(tempItems)
    }

    private fun handleItemClick(item: SubMapItem) {
        if (item.status != CompletionStatus.NOT_STARTED) return

        try {
            when (item.type) {
                SubMapType.QUESTION -> {
                    if (preferenceManager.getHearts() <= 0) {
                        Toast.makeText(this, "Bạn đã hết mạng! Hãy chờ hồi phục hoặc mua thêm.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val intent = Intent(this, QuizActivity::class.java)
                    intent.putExtra("CATEGORY", item.category)
                    intent.putExtra("LEVEL_ID", levelId)
                    startActivityForResult(intent, 1001)
                }
                SubMapType.SPIN_WHEEL -> {
                    val intent = Intent(this, SpinWheelActivity::class.java)
                    intent.putExtra("LEVEL_ID", levelId)
                    startActivityForResult(intent, 1004)
                }
                SubMapType.TREASURE -> {
                    val intent = Intent(this, TreasureActivity::class.java)
                    intent.putExtra("LEVEL_ID", levelId)
                    startActivityForResult(intent, 1003)
                }
                SubMapType.FLIP_CARD -> {
                    val intent = Intent(this, FlipCardActivity::class.java)
                    intent.putExtra("LEVEL_ID", levelId)
                    startActivityForResult(intent, 1002)
                }
                else -> {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (lastClickedPosition != -1) {
            val item = mapItems.getOrNull(lastClickedPosition) ?: return
            
            var stateChanged = false
            when (requestCode) {
                1001 -> { // QUIZ
                    if (resultCode == RESULT_OK) { // Trả lời đúng
                        currentScore += scorePerCorrect
                        mapItems[lastClickedPosition] = item.copy(status = CompletionStatus.CORRECT)
                        stateChanged = true
                    } else if (resultCode == 2) { // Trả lời sai (RESULT_ANSWER_WRONG)
                        currentScore -= scorePerIncorrect
                        mapItems[lastClickedPosition] = item.copy(status = CompletionStatus.INCORRECT)
                        stateChanged = true
                        preferenceManager.useHeart()
                    }
                }
                1002, 1003, 1004 -> { // MINIGAMES
                    val interacted = data?.getBooleanExtra("INTERACTED", false) ?: false
                    if (resultCode == RESULT_OK && interacted) { // Đã tương tác
                        currentScore = preferenceManager.getLevelScore(levelId)
                        mapItems[lastClickedPosition] = item.copy(status = CompletionStatus.BONUS)
                        stateChanged = true
                    }
                }
            }

            if (stateChanged) {
                preferenceManager.saveLevelScore(levelId, currentScore)
                saveMapState()
                adapter.notifyItemChanged(lastClickedPosition)
                updateUI()
                checkLevelCompletion()
            }
            lastClickedPosition = -1
        }
    }

    override fun onResume() {
        super.onResume()
        loadMapState()
        updateUI() 
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
        updateHandler.post(updateRunnable)
    }
    
    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
        updateHandler.removeCallbacks(updateRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("LAST_CLICKED_POSITION", lastClickedPosition)
    }

    private fun checkLevelCompletion() {
        val questions = mapItems.filter { it?.type == SubMapType.QUESTION }
        val allQuestionsDone = questions.all { it?.status != CompletionStatus.NOT_STARTED }
        val atLeastOneStar = currentScore >= star1Score

        if (allQuestionsDone) {
            if (atLeastOneStar) {
                Toast.makeText(this, "Ải đã hoàn thành!", Toast.LENGTH_LONG).show()
                val mainPrefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
                val currentMax = mainPrefs.getInt("CURRENT_UNLOCKED_LEVEL", 1)
                
                var starsEarnedInThisLevel = 0
                if (currentScore >= star3Score) starsEarnedInThisLevel = 3
                else if (currentScore >= star2Score) starsEarnedInThisLevel = 2
                else if (currentScore >= star1Score) starsEarnedInThisLevel = 1
                
                val oldBestStars = mainPrefs.getInt("STARS_LEVEL_$levelId", 0)
                if (starsEarnedInThisLevel > oldBestStars) {
                    val totalStars = mainPrefs.getInt("total_stars_all_levels", 0)
                    mainPrefs.edit()
                        .putInt("STARS_LEVEL_$levelId", starsEarnedInThisLevel)
                        .putInt("total_stars_all_levels", totalStars + (starsEarnedInThisLevel - oldBestStars))
                        .apply()
                    updateUI()
                }

                if (levelId == currentMax) {
                    mainPrefs.edit().putInt("CURRENT_UNLOCKED_LEVEL", levelId + 1).apply()
                    preferenceManager.Dk_batmo_xn("nv2", true)
                }
            } else {
                Toast.makeText(this, "Chưa đủ điểm ($star1Score) để qua ải!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
