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

class SubMapActivity : AppCompatActivity() {

    private lateinit var rvSubMap: RecyclerView
    private lateinit var adapter: SubMapAdapter
    private lateinit var pbStarProgress: ProgressBar
    private lateinit var tvTotalStars: TextView
    private lateinit var tvTotalCoins: TextView
    private lateinit var starIcons: List<ImageView>

    private val columns = 7
    private val rows = 9
    private var levelId: Int = 1
    private var mapItems = mutableListOf<SubMapItem?>()
    private var lastClickedPosition: Int = -1

    private var currentScore: Int = 0
    private val scorePerCorrect = 25
    private val scorePerIncorrect = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_map)

        levelId = intent.getIntExtra("LEVEL_ID", 1)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        tvTotalStars = findViewById(R.id.head_text_star)
        tvTotalCoins = findViewById(R.id.head_text_coin)
        
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
            generateMapWithShape()
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
        val mainPrefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
        
        // Cập nhật Xu (Coins) và Sao (Stars) vào Header
        tvTotalCoins.text = mainPrefs.getInt("current_coins", 0).toString()
        tvTotalStars.text = currentScore.toString()

        val progress = currentScore.coerceIn(0, 100)
        pbStarProgress.progress = progress

        starIcons[0].setImageResource(if (progress >= 33) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
        starIcons[1].setImageResource(if (progress >= 66) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
        starIcons[2].setImageResource(if (progress >= 100) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
    }

    private fun generateMapWithShape() {
        val allCategories = mutableListOf(
            "AmNhac", "ChoiChu", "CNXHKH", "DiaLy", "DoVui",
            "TiengAnh", "HoaHoc", "KienThucChung", "KinhTeChinhTri",
            "LichSu", "TinHoc", "TuTuongHCM", "VanHoc", "VatLy"
        ).shuffled()

        // Định nghĩa các hình thù dựa trên tọa độ (x, y)
        val robotShape = listOf(
            Pair(3,0), Pair(3,1), // Đầu
            Pair(2,2), Pair(3,2), Pair(4,2), // Vai
            Pair(1,3), Pair(2,3), Pair(3,3), Pair(4,3), Pair(5,3), // Thân trên + Tay
            Pair(2,4), Pair(3,4), Pair(4,4), // Bụng
            Pair(2,5), Pair(4,5), Pair(2,6), Pair(4,6)  // Chân
        )

        val flowerShape = listOf(
            Pair(3,3), // Nhụy
            Pair(3,2), Pair(4,2), Pair(4,3), Pair(4,4), Pair(3,4), Pair(2,4), Pair(2,3), Pair(2,2), // Cánh hoa
            Pair(3,5), Pair(3,6), Pair(3,7), // Cành
            Pair(2,6), Pair(4,6), // Lá
            Pair(1,1), Pair(5,1), Pair(1,5) // Phụ
        )

        val towerShape = listOf(
            Pair(3,1),
            Pair(3,2), Pair(2,2), Pair(4,2),
            Pair(3,3), Pair(1,3), Pair(5,3),
            Pair(3,4), Pair(2,4), Pair(4,4),
            Pair(3,5), Pair(0,5), Pair(6,5),
            Pair(3,6), Pair(1,6), Pair(5,6), Pair(3,7)
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
                    val intent = Intent(this, QuizActivity::class.java)
                    intent.putExtra("CATEGORY", item.category)
                    intent.putExtra("LEVEL_ID", levelId)
                    startActivityForResult(intent, 1001)
                }
                SubMapType.SPIN_WHEEL -> {
                    startActivity(Intent(this, com.example.quizmon.ui.level.SpinWheelActivity::class.java))
                    markSpecialItemDone(item)
                }
                SubMapType.TREASURE -> {
                    startActivity(Intent(this, com.example.quizmon.ui.level.TreasureActivity::class.java))
                    markSpecialItemDone(item)
                }
                SubMapType.FLIP_CARD -> {
                    startActivity(Intent(this, com.example.quizmon.ui.level.FlipCardActivity::class.java))
                    markSpecialItemDone(item)
                }
                else -> {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi khi mở trang", Toast.LENGTH_SHORT).show()
        }
    }

    private fun markSpecialItemDone(item: SubMapItem) {
        val pos = mapItems.indexOfFirst { it?.id == item.id }
        if (pos != -1) {
            mapItems[pos] = item.copy(status = CompletionStatus.CORRECT)
            adapter.notifyItemChanged(pos)
            saveMapState()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (lastClickedPosition != -1) {
            val item = mapItems[lastClickedPosition] ?: return
            if (item.type == SubMapType.QUESTION) {
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
    }

    private fun checkLevelCompletion() {
        val questions = mapItems.filter { it?.type == SubMapType.QUESTION }
        val allQuestionsDone = questions.all { it?.status != CompletionStatus.NOT_STARTED }
        val atLeastOneStar = currentScore >= 33

        if (allQuestionsDone) {
            if (atLeastOneStar) {
                Toast.makeText(this, "Ải đã hoàn thành!", Toast.LENGTH_LONG).show()
                val mainPrefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
                val currentMax = mainPrefs.getInt("CURRENT_UNLOCKED_LEVEL", 1)
                val coinManager = com.example.quizmon.ui.shop.PreferenceManager(this)
                if (levelId == currentMax) {
                    mainPrefs.edit().putInt("CURRENT_UNLOCKED_LEVEL", levelId + 1).apply()
                    coinManager.Dk_Ainho_Addcoin("nv2", true)
                }
            } else {
                Toast.makeText(this, "Chưa đủ điểm (1 sao) để qua ải!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
