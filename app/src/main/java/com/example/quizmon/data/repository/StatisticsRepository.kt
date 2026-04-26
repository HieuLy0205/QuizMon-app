package com.example.quizmon.data.repository

import android.content.Context
import com.example.quizmon.data.model.OverallStatistics
import com.example.quizmon.data.model.Statistics
import com.example.quizmon.data.source.local.StatisticsLocalDataSource
import java.util.*

/**
 * Repository quản lý toàn bộ dữ liệu thống kê.
 * Đã sửa lỗi đồng bộ với DataSource và Model.
 */
class StatisticsRepository(context: Context) {

    private val localDataSource = StatisticsLocalDataSource(context)

    /**
     * Lưu kết quả phiên chơi Quiz
     */
    fun saveQuizResult(correct: Int, wrong: Int, date: Date = Date()) {
        val existingStats = localDataSource.getDailyStats(date)
        val newStats = if (existingStats != null) {
            Statistics(
                date = date,
                correctAnswers = existingStats.correctAnswers + correct,
                wrongAnswers = existingStats.wrongAnswers + wrong,
                totalQuestions = existingStats.totalQuestions + correct + wrong,
                streak = existingStats.streak
            )
        } else {
            Statistics(
                date = date,
                correctAnswers = correct,
                wrongAnswers = wrong,
                totalQuestions = correct + wrong,
                streak = 0
            )
        }
        localDataSource.saveDailyStats(newStats)
        updateStreak(date)
    }

    private fun updateStreak(currentDate: Date) {
        val yesterday = Calendar.getInstance().apply {
            time = currentDate
            add(Calendar.DAY_OF_YEAR, -1)
        }.time

        val yesterdayStats = localDataSource.getDailyStats(yesterday)
        val currentStreak = localDataSource.getCurrentStreak()

        val newStreak = if (yesterdayStats != null && yesterdayStats.totalQuestions > 0) {
            currentStreak + 1
        } else {
            1
        }

        localDataSource.saveCurrentStreak(newStreak)
        localDataSource.saveLongestStreak(newStreak)
    }

    /**
     * Lấy dữ liệu tổng quát cho UI (StatisticsFragment)
     */
    fun getOverallStatistics(): OverallStatistics {
        val allStats: List<Statistics> = localDataSource.getAllStats()
        
        // Sửa lỗi Unresolved reference 'it' bằng cách chỉ định rõ kiểu dữ liệu
        val totalCorrect = allStats.sumOf { s: Statistics -> s.correctAnswers }
        val totalQuestions = allStats.sumOf { s: Statistics -> s.totalQuestions }
        
        val currentStreak = localDataSource.getCurrentStreak()
        val longestStreak = localDataSource.getLongestStreak()
        val levelsCompleted = localDataSource.getLevelsCompleted()
        val startDate = Date(localDataSource.getStartDate())

        return OverallStatistics(
            totalCorrect = totalCorrect,
            totalQuestions = totalQuestions,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            levelsCompleted = levelsCompleted,
            startDate = startDate
        )
    }

    /**
     * Lấy thống kê 7 ngày gần nhất cho biểu đồ
     */
    fun getLast7DaysStats(): List<Statistics> {
        val statsList = mutableListOf<Statistics>()

        for (i in 6 downTo 0) {
            val date = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }.time

            val stats = localDataSource.getDailyStats(date)
            if (stats != null) {
                statsList.add(stats)
            } else {
                statsList.add(Statistics(date, 0, 0, 0, 0))
            }
        }
        return statsList
    }
}
