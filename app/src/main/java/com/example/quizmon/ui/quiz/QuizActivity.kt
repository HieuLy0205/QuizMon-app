package com.example.quizmon.ui.quiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

class QuizActivity : AppCompatActivity() {

    private var currentQuestion: Question? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val category = intent.getStringExtra("CATEGORY") ?: "CNXHKH"
        val levelId = intent.getIntExtra("LEVEL_ID", 1)
        
        loadQuestionById(category, levelId)

        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val buttons = listOf<Button>(
            findViewById(R.id.btnA),
            findViewById(R.id.btnB),
            findViewById(R.id.btnC),
            findViewById(R.id.btnD)
        )

        currentQuestion?.let { q ->
            tvQuestion.text = q.question
            buttons.forEachIndexed { index, button ->
                if (index < q.options.size) {
                    button.text = q.options[index]
                    button.visibility = View.VISIBLE
                    button.setOnClickListener { handleAnswer(index) }
                } else {
                    button.visibility = View.GONE
                }
            }
        } ?: run {
            Toast.makeText(this, "Không tìm thấy câu hỏi với ID $levelId cho chủ đề $category!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadQuestionById(category: String, targetId: Int) {
        try {
            val fileName = when (category) {
                "KienThucChung" -> "KTC_questions.json"
                "CNXHKH" -> "CNXHKH_questions.json"
                "DiaLy" -> "D_questions.json"
                "HoaHoc" -> "H_questions.json"
                "KinhTeChinhTri" -> "KTCT_questions.json"
                "LichSu" -> "S_questions.json"
                "TinHoc" -> "TH_questions.json"
                "TuTuongHCM" -> "TTHCM_questions.json"
                "VanHoc" -> "V_questions.json"
                "VatLy" -> "VL_questions.json"
                "AmNhac" -> "AN_questions.json"
                "ChoiChu" -> "CC_questions.json"
                "TiengAnh" -> "E_questions.json"
                "DoVui" -> "DoVui_questions.json"
                else -> "questions.json"
            }
            
            val inputStream = assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Question>>() {}.type
            val questions: List<Question> = Gson().fromJson(reader, type)
            
            // Tìm câu hỏi theo ID. 
            // So sánh linh hoạt vì id trong JSON có thể là String hoặc Number
            currentQuestion = questions.find { it.id.toString() == targetId.toString() } 
                ?: questions.random()
            
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback nếu có lỗi đọc file
        }
    }

    private fun handleAnswer(selectedIndex: Int) {
        val isCorrect = selectedIndex == currentQuestion?.correctIndex
        
        if (isCorrect) {
            Toast.makeText(this, "Chính xác!\n${currentQuestion?.explanation}", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
        } else {
            Toast.makeText(this, "Sai rồi!\nĐáp án đúng: ${currentQuestion?.options?.get(currentQuestion!!.correctIndex)}", Toast.LENGTH_LONG).show()
            setResult(RESULT_CANCELED)
        }

        android.os.Handler(mainLooper).postDelayed({
            finish()
        }, 2000)
    }
}
