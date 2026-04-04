package com.example.quizmon.ui.pet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizmon.R

class PetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Gắn layout cho màn hình Pet
        setContentView(R.layout.activity_pet)
    }
}