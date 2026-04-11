package com.example.quizmon.data.repository

import android.content.Context
import com.example.quizmon.data.model.OverallStatistics
import com.example.quizmon.data.model.Statistics
import com.example.quizmon.data.source.local.StatisticsLocalDataSource
import java.util.*

class StatisticsRepository(context: Context) {

    private val localDataSource = StatisticsLocalDataSource(context)

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
        val calendar = Calendar.getInstance()
        calendar.time = currentDate

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
        val totalWrong = allStats.sumOf { it.wrongAnswers }
        val totalQuestions = allStats.sumOf { it.totalQuestions }
        val currentStreak = localDataSource.getCurrentStreak()
        val longestStreak = localDataSource.getLongestStreak()

        return OverallStatistics(
            totalCorrect = totalCorrect,
            totalWrong = totalWrong,
            totalQuestions = totalQuestions,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }

    fun getLast7DaysStats(): List<Statistics> {
        val statsList = mutableListOf<Statistics>()
        val calendar = Calendar.getInstance()

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