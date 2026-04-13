package com.example.quizmon.data.model

data class Pet(
    val id: String,
    val name: String,
    val level: Int,
    val exp: Int,
    val imageResIds: List<Int>,
    val type: String
)