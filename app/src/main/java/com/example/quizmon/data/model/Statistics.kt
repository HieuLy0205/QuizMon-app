package com.example.quizmon.data.model

import java.util.Date

/**
 * Thống kê chi tiết theo từng ngày (Dùng cho biểu đồ hoặc danh sách lịch sử)
 */
data class Statistics(
    val date: Date,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val streak: Int = 0
) {
    // Tỉ lệ chính xác của ngày đó
    val accuracyRate: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
}

/**
 * Thống kê tổng quát (Hiển thị trên các Card ở màn hình Statistics)
 */
data class OverallStatistics(
    val totalCorrect: Int = 0,
    val totalWrong: Int = 0,
    val totalQuestions: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val levelsCompleted: Int = 0, // Số màn chơi đã hoàn thành
    val startDate: Date? = null    // Ngày bắt đầu tham gia (cho mục "Kể từ...")
) {
    // Tỉ lệ chính xác tổng quát (%)
    val overallAccuracy: Int
        get() = if (totalQuestions > 0) (totalCorrect * 100) / totalQuestions else 0
}

/**
 * Thống kê theo từng chủ đề/danh mục (Hiển thị ở danh sách phía dưới)
 */
data class CategoryStatistics(
    val categoryId: Int,
    val categoryName: String,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val iconResId: Int? = null // Resource ID của icon thể loại
) {
    // Tỉ lệ hoàn thành của category này
    val accuracyRate: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
}