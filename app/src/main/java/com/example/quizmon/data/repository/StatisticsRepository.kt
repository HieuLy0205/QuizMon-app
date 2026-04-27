package com.example.quizmon.data.repository

import android.content.Context
import com.example.quizmon.data.model.Achievement
import com.example.quizmon.data.model.OverallStatistics
import com.example.quizmon.data.model.Statistics
import com.example.quizmon.data.source.local.StatisticsLocalDataSource
import java.util.*

class StatisticsRepository(context: Context) {

    private val localDataSource = StatisticsLocalDataSource(context)

    fun saveQuizResult(correct: Int, wrong: Int, isHard: Boolean = false, date: Date = Date()) {
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
        localDataSource.addQuestionsAnswered(correct + wrong)
        
        if (correct > wrong) { // Giả sử thắng khi đúng > sai
            localDataSource.incrementMatchesWon()
        }
        
        if (isHard && correct > wrong) {
            localDataSource.incrementHardLevels()
        }
        
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

    fun getOverallStatistics(): OverallStatistics {
        val allStats = localDataSource.getAllStats()
        val totalCorrect = allStats.sumOf { it.correctAnswers }
        val totalQuestions = allStats.sumOf { it.totalQuestions }
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
     * Lấy danh sách thành tích với tiến độ thực tế
     */
    fun getAchievements(): List<Achievement> {
        val matchesWon = localDataSource.getMatchesWon()
        val currentStreak = localDataSource.getCurrentStreak()
        val petsCollected = localDataSource.getPetsCollected()
        val totalQuestions = localDataSource.getTotalQuestionsAnswered()
        val hardLevels = localDataSource.getHardLevelsCompleted()

        return listOf(
            Achievement("1", "Chiến binh", "Thắng 100 trận đấu", matchesWon, 100, matchesWon >= 100),
            Achievement("2", "Người hâm mộ", "Đăng nhập 7 ngày liên tiếp", currentStreak, 7, currentStreak >= 7),
            Achievement("3", "Thợ săn", "Thu thập 10 loại thú cưng", petsCollected, 10, petsCollected >= 10),
            Achievement("4", "Đam mê", "Trả lời 500 câu hỏi", totalQuestions, 500, totalQuestions >= 500),
            Achievement("5", "Bậc thầy", "Hoàn thành 20 ải khó", hardLevels, 20, hardLevels >= 20)
        )
    }
}