package com.example.quizmon.data.repository

import android.content.Context
import com.example.quizmon.data.model.Question
import com.example.quizmon.data.model.Topic
import com.example.quizmon.data.source.JsonLoader

class QuizRepository(context: Context) {

    private val jsonLoader = JsonLoader(context)

    /**
     * Lấy danh sách chủ đề
     */
    fun getTopics(): List<Topic> {
        return jsonLoader.getTopics()
    }

    /**
     * Lấy câu hỏi theo chủ đề, shuffle và giới hạn số lượng
     */
    fun getQuestionsByTopic(fileName: String, limit: Int = 10): List<Question> {
        return jsonLoader.getQuestionsByFile(fileName)
            .shuffled()
            .take(limit)
    }

    /**
     * Lấy toàn bộ câu hỏi từ tất cả chủ đề
     */
    fun getAllQuestions(): List<Question> {
        return jsonLoader.getAllQuestions()
    }

    /**
     * Lấy câu hỏi ngẫu nhiên từ tất cả chủ đề
     */
    fun getRandomQuestions(limit: Int = 10): List<Question> {
        return jsonLoader.getAllQuestions()
            .shuffled()
            .take(limit)
    }
}