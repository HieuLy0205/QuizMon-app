package com.example.quizmon.data.source

import android.content.Context
import com.example.quizmon.data.model.Question
import com.example.quizmon.data.model.Topic
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class JsonLoader(private val context: Context) {
    private val gson = Gson()

    /**
     * Lấy danh sách các chủ đề. 
     * Nếu questions.json trống, sẽ trả về danh sách được định nghĩa sẵn 
     * ứng với các tệp *_questions.json đã tạo.
     */
    fun getTopics(): List<Topic> {
        val topics = try {
            val jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Topic>>() {}.type
            val list: List<Topic>? = gson.fromJson(jsonString, listType)
            if (list.isNullOrEmpty()) getStaticTopics() else list
        } catch (e: Exception) {
            getStaticTopics()
        }
        return topics
    }

    /**
     * Lấy toàn bộ câu hỏi từ tất cả các tệp *_questions.json trong assets.
     */
    fun getAllQuestions(): List<Question> {
        val allQuestions = mutableListOf<Question>()
        try {
            val files = context.assets.list("") ?: return emptyList()
            files.filter { it.endsWith("_questions.json") }.forEach { fileName ->
                allQuestions.addAll(getQuestionsByFile(fileName))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return allQuestions
    }

    /**
     * Lấy danh sách câu hỏi từ một tệp cụ thể.
     */
    fun getQuestionsByFile(fileName: String): List<Question> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Question>>() {}.type
            gson.fromJson(jsonString, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Danh sách các chủ đề mặc định tương ứng với các tệp đã tạo.
     */
    private fun getStaticTopics(): List<Topic> {
        return listOf(
            Topic(1, "Kiến thức chung + Toán", "KTC_questions.json"),
            Topic(2, "Văn học", "V_questions.json"),
            Topic(3, "Lịch sử", "S_questions.json"),
            Topic(4, "Địa lý", "D_questions.json"),
            Topic(5, "Hóa học", "H_questions.json"),
            Topic(6, "Tiếng anh", "E_questions.json"),
            Topic(7, "Đố vui", "DoVui_questions.json"),
            Topic(8, "Chơi chữ", "CC_questions.json"),
            Topic(9, "Vật Lý", "VL_questions.json"),
            Topic(10, "Âm nhạc", "AN_questions.json"),
            Topic(11, "Tin học", "TH_questions.json"),
            Topic(12, "Kinh tế chính trị", "KTCT_questions.json"),
            Topic(13, "Tư tưởng Hồ Chí Minh", "TTHCM_questions.json"),
            Topic(14, "Chủ nghĩa xã hội khoa học", "CNXHKH_questions.json")
        )
    }
}
