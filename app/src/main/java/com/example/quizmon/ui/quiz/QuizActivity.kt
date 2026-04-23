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
//import com.example.quizmon.data.model.Question

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
        loadRandomQuestion(category)

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
            Toast.makeText(this, "Không tìm thấy câu hỏi!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadRandomQuestion(category: String) {
        try {
            val fileName = when (category) {
                "Toan" -> "KT_Toan_questions.json"
                "CNKHXH" -> "CNXHKH_questions.json"
                "DiaLy" -> "DoVui_questions.json" // Tạm thời dùng DoVui nếu chưa có DiaLy
                else -> "S_questions.json"
            }
            
            val inputStream = assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Question>>() {}.type
            val questions: List<Question> = Gson().fromJson(reader, type)
            currentQuestion = questions.random()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
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
        }, 2000) // Tăng thời gian lên 2s để người dùng kịp đọc giải thích
    }
}
