package com.example.quizmon.ui.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizmon.R

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Gắn layout cho màn hình Quiz
        setContentView(R.layout.activity_quiz)
    }
}