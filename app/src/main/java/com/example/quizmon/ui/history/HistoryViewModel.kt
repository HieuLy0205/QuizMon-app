package com.example.quizmon.ui.history

import android.app.Application
import androidx.lifecycle.*
import com.example.quizmon.data.model.Question
import com.example.quizmon.data.source.local.AppDatabase
import com.example.quizmon.data.source.local.HistoryRecord
import com.example.quizmon.data.repository.HistoryRepository
import kotlinx.coroutines.launch


class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository

    // Trạng thái bộ lọc đang áp dụng
    private val _selectedCategory   = MutableLiveData("all")
    private val _selectedAnswerType = MutableLiveData("all")
    val selectedCategory  : LiveData<String> = _selectedCategory
    val selectedAnswerType: LiveData<String> = _selectedAnswerType

    // Trigger để switchMap reload khi filter thay đổi
    private val _filterTrigger = MutableLiveData(Pair("all", "all"))

    val historyList: LiveData<List<HistoryRecord>> = _filterTrigger.switchMap { (cat, ans) ->
        repository.getFiltered(cat, ans)
    }

    // Danh sách thể loại đã chơi (động từ DB)
    private val _categories = MutableLiveData<List<String>>(emptyList())
    val categories: LiveData<List<String>> = _categories

    val isFiltered: Boolean
        get() = _selectedCategory.value != "all" || _selectedAnswerType.value != "all"

    init {
        val db = AppDatabase.getInstance(application)
        repository = HistoryRepository(db.historyDao())
        refreshCategories()
    }

    private fun refreshCategories() {
        viewModelScope.launch {
            _categories.value = repository.getDistinctCategories()
        }
    }

    fun applyFilter(category: String, answerType: String) {
        _selectedCategory.value   = category
        _selectedAnswerType.value = answerType
        _filterTrigger.value      = Pair(category, answerType)
        refreshCategories()
    }

    fun resetFilter() = applyFilter("all", "all")

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
            refreshCategories()
        }
    }

    // -------------------------------------------------------------------------
    // Gọi hàm này từ QuizActivity ngay sau khi người chơi bấm Xác nhận đáp án
    // -------------------------------------------------------------------------

//     question       Object Question đang hiển thị
//      chosenIndex    Index đáp án người chơi đã chọn (selectedIndex trong QuizActivity)
//       categoryDisplay Tên hiển thị thể loại — lấy từ getCategoryDisplayName(currentCategory)
//                            VD: "Âm nhạc", "Lịch sử", "Vật lý"

    fun saveAnswer(
        question        : Question,
        chosenIndex     : Int,
        categoryDisplay : String
    ) {
        viewModelScope.launch {
            repository.insert(
                HistoryRecord(
                    questionId        = question.id,
                    questionText      = question.question,
                    optionsSerialized = question.options.joinToString("||"),
                    correctIndex      = question.correctIndex,
                    chosenIndex       = chosenIndex,
                    explanation       = question.explanation,
                    category          = categoryDisplay
                )
            )
            refreshCategories()
        }
    }
}