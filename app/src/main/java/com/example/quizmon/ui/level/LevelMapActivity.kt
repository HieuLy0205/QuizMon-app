package com.example.quizmon.ui.level

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.ui.quiz.QuizActivity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_map)

        val ivBackground1 = findViewById<ImageView>(R.id.ivBackground1)
        val ivBackground2 = findViewById<ImageView>(R.id.ivBackground2)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val rvLevelMap = findViewById<RecyclerView>(R.id.rvLevelMap)
        
        val totalLevels = 500
        val levels = (1..totalLevels).toList().reversed()
        val currentUnlockedLevel = 1

        val adapter = LevelAdapter(levels, currentUnlockedLevel) { selectedLevel ->
            Toast.makeText(this, "Bắt đầu ải $selectedLevel", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("LEVEL_ID", selectedLevel)
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvLevelMap.layoutManager = layoutManager
        rvLevelMap.adapter = adapter
        
        rvLevelMap.scrollToPosition(totalLevels - currentUnlockedLevel)

        // Lắng nghe sự kiện cuộn để thay đổi background
        rvLevelMap.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val offset = recyclerView.computeVerticalScrollOffset()
                val range = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent()
                
                if (range > 0) {
                    // Tính toán progress từ 0.0 đến 1.0
                    val progress = offset.toFloat() / range
                    
                    // Tính toán index của background hiện tại và kế tiếp
                    val maxIndex = backgroundResIds.size - 1
                    val floatIndex = progress * maxIndex
                    val currentIndex = floatIndex.toInt().coerceIn(0, maxIndex)
                    val nextIndex = (currentIndex + 1).coerceAtMost(maxIndex)
                    
                    // Tính toán alpha cho việc cross-fade
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
}