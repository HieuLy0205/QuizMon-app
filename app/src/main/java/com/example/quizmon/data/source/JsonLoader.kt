package com.example.quizmon.data.source

import android.content.Context
import com.example.quizmon.data.model.Question
import com.example.quizmon.data.model.Topic
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonLoader(private val context: Context) {
    private val gson = Gson()

    fun getTopics(): List<Topic> {
        return try {
            val jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Topic>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getQuestionsByFile(fileName: String): List<Question> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Question>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
}