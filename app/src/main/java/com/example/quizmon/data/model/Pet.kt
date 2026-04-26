package com.example.quizmon.data.model

data class Pet(
    //mã định danh (1)
    val id: String,
    //tên thú cưng
    val name: String,
    val currentelevel: Int,
    val levelmax: Int,
    //cấp độ
    val animetor: Map<Int, IntArray>,

)