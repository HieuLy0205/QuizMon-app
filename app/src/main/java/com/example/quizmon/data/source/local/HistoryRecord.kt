package com.example.quizmon.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "history")
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

//     ID câu hỏi từ JSON
    val questionId: Int,

//  Nội dung câu hỏi (field "question")
    val questionText: String,

    val optionsSerialized: String,

//    Index đáp án đúng (field "correctIndex", 0-based)
    val correctIndex: Int,

//    Index đáp án người chơi đã chọn (0-based)
    val chosenIndex: Int,

//     Giải thích đáp án
    val explanation: String,


    val category: String,

//     Thời điểm trả lời
    val answeredAt: Long = System.currentTimeMillis()
) {
//    true nếu người chơi chọn đúng
    val isCorrect: Boolean get() = chosenIndex == correctIndex

//     Parse lại List<String> từ chuỗi đã serialize
    fun getOptions(): List<String> = optionsSerialized.split("||")
}