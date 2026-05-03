package com.example.quizmon.data.repository
import androidx.lifecycle.LiveData
import com.example.quizmon.data.source.local.HistoryDao
import com.example.quizmon.data.source.local.HistoryRecord


class HistoryRepository(private val dao: HistoryDao) {

    fun getFiltered(category: String, answerType: String): LiveData<List<HistoryRecord>> =
        dao.getFiltered(category, answerType)

    suspend fun insert(record: HistoryRecord) = dao.insert(record)

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun getDistinctCategories(): List<String> = dao.getDistinctCategories()
}