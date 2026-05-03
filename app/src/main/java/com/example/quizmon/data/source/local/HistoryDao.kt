package com.example.quizmon.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: HistoryRecord)


//      category   = "all" → không lọc thể loại
//      answerType = "all"     → tất cả
//      answerType = "correct" → chỉ câu đúng
//     *answerType = "wrong"   → chỉ câu sai
//
    @Query("""
        SELECT * FROM history
        WHERE (:category = 'all' OR category = :category)
          AND (
              :answerType = 'all'
              OR (:answerType = 'correct' AND chosenIndex = correctIndex)
              OR (:answerType = 'wrong'   AND chosenIndex != correctIndex)
          )
        ORDER BY answeredAt DESC
    """)
    fun getFiltered(category: String, answerType: String): LiveData<List<HistoryRecord>>

//    Danh sách thể loại đã có trong lịch sử (dùng cho chip lọc động)
    @Query("SELECT DISTINCT category FROM history ORDER BY category ASC")
    suspend fun getDistinctCategories(): List<String>

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}