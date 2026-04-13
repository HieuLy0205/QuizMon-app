package com.example.quizmon.ui.level

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R

class LevelMapActivity : AppCompatActivity() {

    private val backgroundResIds = intArrayOf(
        R.drawable.bg_map,
        R.drawable.bg_map_1,
        R.drawable.bg_map_2,
        R.drawable.bg_map_3,
        R.drawable.bg_map_4,
        R.drawable.bg_map_5,
        R.drawable.bg_map_6,
        R.drawable.bg_map_7,
        R.drawable.bg_map_8
    )

    private var currentUnlockedLevel = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_map)

        loadProgress()

        val ivBackground1 = findViewById<ImageView>(R.id.ivBackground1)
        val ivBackground2 = findViewById<ImageView>(R.id.ivBackground2)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val rvLevelMap = findViewById<RecyclerView>(R.id.rvLevelMap)
        
        val totalLevels = 200
        val levels = (1..totalLevels).toList().reversed()

        val adapter = LevelAdapter(levels, currentUnlockedLevel) { selectedLevel ->
            if (selectedLevel <= currentUnlockedLevel) {
                val intent = Intent(this, SubMapActivity::class.java)
                intent.putExtra("LEVEL_ID", selectedLevel)
                startActivity(intent)
            }
        }

        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvLevelMap.layoutManager = layoutManager
        rvLevelMap.adapter = adapter
        
        rvLevelMap.scrollToPosition(totalLevels - currentUnlockedLevel)

        rvLevelMap.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val offset = recyclerView.computeVerticalScrollOffset()
                val range = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()
                
                if (range > 0) {
                    val progress = offset.toFloat() / range
                    val maxIndex = backgroundResIds.size - 1
                    val floatIndex = progress * maxIndex
                    val currentIndex = floatIndex.toInt().coerceIn(0, maxIndex)
                    val nextIndex = (currentIndex + 1).coerceAtMost(maxIndex)
                    val alpha = floatIndex - currentIndex
                    
                    ivBackground1.setImageResource(backgroundResIds[currentIndex])
                    ivBackground1.alpha = 1f - alpha
                    
                    if (currentIndex != nextIndex) {
                        ivBackground2.setImageResource(backgroundResIds[nextIndex])
                        ivBackground2.alpha = alpha
                    } else {
                        ivBackground2.alpha = 0f
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật lại level khi quay về từ SubMapActivity
        val oldLevel = currentUnlockedLevel
        loadProgress()
        if (oldLevel != currentUnlockedLevel) {
            recreate() // Refresh UI
        }
    }

    private fun loadProgress() {
        val prefs = getSharedPreferences("QuizMonPrefs", Context.MODE_PRIVATE)
        currentUnlockedLevel = prefs.getInt("CURRENT_UNLOCKED_LEVEL", 1)
    }
}
