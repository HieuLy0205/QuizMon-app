package com.example.quizmon.data.model

import java.util.Date

/**
 * Thống kê hàng ngày
 */
data class Statistics(
    val date: Date,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val streak: Int = 0
) {
    val accuracyRate: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
}

/**
 * Thống kê tổng quát cho UI
 */
data class OverallStatistics(
    val totalCorrect: Int = 0,
    val totalQuestions: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val levelsCompleted: Int = 0,
    val startDate: Date? = null
) {
    val overallAccuracy: Int
        get() = if (totalQuestions > 0) (totalCorrect * 100) / totalQuestions else 0
}

/**
 * Model cho thành tích (Achievement)
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val isCompleted: Boolean
)
