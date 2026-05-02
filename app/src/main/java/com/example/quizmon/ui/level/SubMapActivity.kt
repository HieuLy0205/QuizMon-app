package com.example.quizmon.ui.level

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
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
import com.example.quizmon.utils.SoundManager
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
    
    // Mốc điểm đạt sao: 1 sao = 90, 2 sao = 180, 3 sao = 270
    private val star1Score = 90
    private val star2Score = 180
    private val star3Score = 270
    private val maxProgressScore = 270

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
        btnBack.setOnClickListener { 
            SoundManager.playClick()
            finish() 
        }

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
        
        // Kiểm tra xem đã hoàn thành từ trước chưa để hiện bảng tổng kết
        checkLevelCompletion()
    }

    private fun saveMapState() {
        val prefs = getSharedPreferences("QuizMonMapPrefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(mapItems)
        // Dùng commit để ghi dữ liệu ngay lập tức
        prefs.edit().putString("MAP_STATE_$levelId", json).putInt("SCORE_$levelId", currentScore).commit()
    }

    private fun updateUI() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
        
        tvCurrentStageScore.text = currentScore.toString()
        
        when {
            currentScore > 0 -> tvCurrentStageScore.setTextColor(Color.parseColor("#4CAF50")) 
            currentScore < 0 -> tvCurrentStageScore.setTextColor(Color.parseColor("#F44336")) 
            else -> tvCurrentStageScore.setTextColor(Color.BLACK)
        }

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

        SoundManager.playClick()
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
                    val isDouble = data?.getBooleanExtra(QuizActivity.EXTRA_IS_DOUBLE_SCORE, false) ?: false
                    if (resultCode == RESULT_OK) { // Trả lời đúng
                        val gained = if (isDouble) scorePerCorrect * 2 else scorePerCorrect
                        currentScore += gained
                        mapItems[lastClickedPosition] = item.copy(status = CompletionStatus.CORRECT)
                        stateChanged = true
                    } else if (resultCode == 2) { // Trả lời sai (RESULT_ANSWER_WRONG)
                        val penalty = if (isDouble) 0 else scorePerIncorrect
                        currentScore -= penalty
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
        
        // Phát nhạc nền
        SoundManager.playMusic(this, R.raw.background)
    }
    
    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
        updateHandler.removeCallbacks(updateRunnable)
        
        SoundManager.pauseMusic()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("LAST_CLICKED_POSITION", lastClickedPosition)
    }

    private fun checkLevelCompletion() {
        val validItems = mapItems.filterNotNull()
        if (validItems.isEmpty()) return

        val questions = validItems.filter { it.type == SubMapType.QUESTION }
        val specials = validItems.filter { it.type == SubMapType.SPIN_WHEEL || it.type == SubMapType.TREASURE || it.type == SubMapType.FLIP_CARD }

        val allQuestionsDone = questions.all { it.status != CompletionStatus.NOT_STARTED }
        val allSpecialDone = specials.all { it.status != CompletionStatus.NOT_STARTED }

        // Nếu TẤT CẢ các ô (Câu hỏi + Đặc biệt) đã được chơi xong
        if (allQuestionsDone && allSpecialDone && questions.isNotEmpty()) {
            val correctAnswers = questions.count { it.status == CompletionStatus.CORRECT }
            val totalQuestions = questions.size
            
            // 1. Cập nhật tiến trình mở ải và lưu sao vào PreferenceManager
            updateUnlockedProgress()

            // 2. Hiển thị bảng tổng kết
            showSummaryDialog(correctAnswers, totalQuestions)
        }
    }

    private fun updateUnlockedProgress() {
        val mainPrefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
        val currentMax = mainPrefs.getInt("CURRENT_UNLOCKED_LEVEL", 1)
        
        var starsEarned = 0
        if (currentScore >= star3Score) starsEarned = 3
        else if (currentScore >= star2Score) starsEarned = 2
        else if (currentScore >= star1Score) starsEarned = 1
        
        // Lưu số sao đạt được cho ải này
        val oldBestStars = mainPrefs.getInt("STARS_LEVEL_$levelId", 0)
        if (starsEarned > oldBestStars) {
            val totalStars = mainPrefs.getInt("total_stars_all_levels", 0)
            mainPrefs.edit()
                .putInt("STARS_LEVEL_$levelId", starsEarned)
                .putInt("total_stars_all_levels", totalStars + (starsEarned - oldBestStars))
                .apply()
        }

        // MỞ KHÓA ẢI TIẾP THEO: Điều kiện là đạt ít nhất 1 sao (90 điểm). 
        // Chỉ tăng currentMax nếu đang chơi ải cao nhất.
        if (currentScore >= star1Score && levelId == currentMax) {
            mainPrefs.edit().putInt("CURRENT_UNLOCKED_LEVEL", levelId + 1).apply()
            preferenceManager.Dk_batmo_xn("nv2", true)
        }
    }

    private fun resetLevel() {
        currentScore = 0
        mapItems.forEachIndexed { index, item ->
            if (item != null) {
                mapItems[index] = item.copy(status = CompletionStatus.NOT_STARTED)
            }
        }
        saveMapState()
        preferenceManager.saveLevelScore(levelId, currentScore)
        updateUI()
        adapter.notifyDataSetChanged()
    }

    private fun showSummaryDialog(correct: Int, total: Int) {
        val overlay = findViewById<View>(R.id.summaryOverlay) ?: return
        
        // Tránh hiển thị chồng chéo nếu đã hiện
        if (overlay.visibility == View.VISIBLE) return 

        SoundManager.playMusic(this, R.raw.victory, false) // Phát nhạc thắng cuộc (không lặp)

        val tvTitle = findViewById<TextView>(R.id.tvSummaryTitle)
        val tvCorrect = findViewById<TextView>(R.id.tvCorrectCount)
//        val tvBonus = findViewById<TextView>(R.id.tvBonusPoints)
        val tvTotalScore = findViewById<TextView>(R.id.tvTotalScore)
        val tvPercent = findViewById<TextView>(R.id.tvPercentile)
        val btnAction = findViewById<Button>(R.id.btnSummaryContinue)
        
        val star1 = findViewById<ImageView>(R.id.ivSumStar1)
        val star2 = findViewById<ImageView>(R.id.ivSumStar2)
        val star3 = findViewById<ImageView>(R.id.ivSumStar3)

        tvTitle.text = "Cấp độ $levelId"
        tvCorrect.text = "$correct/$total"
        
//        // Điểm thưởng = Tổng điểm - (Số câu đúng * 30đ mỗi câu)
//        val bonusValue = (currentScore - (correct * 30)).coerceAtLeast(0)
//        tvBonus.text = bonusValue.toString()
//
        tvTotalScore.text = currentScore.toString()

        val percent = ((currentScore.toFloat() / star3Score) * 100).toInt().coerceIn(40, 99)
        
        if (currentScore >= star1Score) {
            tvPercent.text = "Bạn đang thể hiện giỏi hơn $percent% số người chơi khác."
            tvPercent.setTextColor(Color.parseColor("#0277BD"))
            btnAction.text = "Tiếp theo"
            btnAction.setOnClickListener {
                SoundManager.playClick()
                val intent = Intent(this, LevelMapActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
        } else {
            tvPercent.text = "Bạn cần đạt ít nhất 90 điểm để mở khóa ải tiếp theo!"
            tvPercent.setTextColor(Color.RED)
            btnAction.text = "Chơi lại"
            btnAction.setOnClickListener {
                SoundManager.playClick()
                resetLevel()
                overlay.animate().alpha(0f).setDuration(300).withEndAction {
                    overlay.visibility = View.GONE
                    // Resume nhạc nền khi đóng dialog
                    SoundManager.playMusic(this@SubMapActivity, R.raw.background)
                }.start()
            }
        }

        // Cập nhật trạng thái sáng/mờ của 3 ngôi sao trong Dialog
        star1.alpha = if (currentScore >= star1Score) 1.0f else 0.2f
        star2.alpha = if (currentScore >= star2Score) 1.0f else 0.2f
        star3.alpha = if (currentScore >= star3Score) 1.0f else 0.2f

        // Hiển thị Overlay với hiệu ứng mờ dần
        overlay.visibility = View.VISIBLE
        overlay.bringToFront()
        overlay.alpha = 0f
        overlay.animate().alpha(1f).setDuration(600).start()
    }
}
