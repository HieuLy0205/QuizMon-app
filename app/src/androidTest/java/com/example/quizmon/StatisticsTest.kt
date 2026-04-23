package com.example.quizmon

import com.example.quizmon.data.model.Statistics
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class StatisticsTest {
    @Test
    fun testAccuracyCalculation() {
        // Giả sử đúng 8, sai 2, tổng 10
        val stats = Statistics(
            date = Date(),
            correctAnswers = 8,
            wrongAnswers = 2,
            totalQuestions = 10
        )

        // Kiểm tra xem accuracyRate có trả về 80 (%) không
        assertEquals(80, stats.accuracyRate)
    }

    @Test
    fun testZeroQuestions() {
        val stats = Statistics(date = Date(), totalQuestions = 0)
        // Kiểm tra tránh lỗi chia cho 0
        assertEquals(0, stats.accuracyRate)
    }
}