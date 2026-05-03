package com.example.quizmon.ui.rank

data class RankItem(
    val rank: Int,
    val name: String,
    val avatar: String,
    val exp: Int,
    val isUser: Boolean = false
)
