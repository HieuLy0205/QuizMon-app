package com.example.quizmon.data.model

data class Pet(
    //mã định danh (1)
    val id: String,
    //tên thú cưng
    val name: String,
    //cấp độ
    val level: Int,
    //điểm kinh nghiệm
    val exp: Int,
    //danh sách ảnh
    val imageResIds: List<Int>,
    //hệ loại pet
    val type: String
)