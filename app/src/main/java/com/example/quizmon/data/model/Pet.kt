package com.example.quizmon.data.model

data class Pet(
    //mã định danh (1),(2)
    val id: String,
    //tên thú cưng
    val name: String,

    val currentelevel: Int,

    val levelmax: Int,

    //hoạt ảnh
    val animetor: Map<Int, IntArray>,

)