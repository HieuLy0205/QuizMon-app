package com.example.quizmon.ui.faq

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R

class FaqDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_detail)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvContent = findViewById<TextView>(R.id.tvContent)

        tvTitle.text = title
        tvContent.text = content
    }
}