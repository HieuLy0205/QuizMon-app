package com.example.quizmon.data.model

import java.util.Date

/**
 * Thống kê hàng ngày - Đảm bảo tên thuộc tính khớp 100% với Repository
 */
data class Statistics(
    val date: Date,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val streak: Int = 0
)

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
