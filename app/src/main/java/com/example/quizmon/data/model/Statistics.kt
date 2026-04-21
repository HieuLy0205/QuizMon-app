package com.example.quizmon.data.model

import java.util.Date

data class Statistics(
    val date: Date,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val totalQuestions: Int,
    val streak: Int
)

data class OverallStatistics(
    val totalCorrect: Int,
    val totalWrong: Int,
    val totalQuestions: Int,
    val currentStreak: Int,
    val longestStreak: Int
)