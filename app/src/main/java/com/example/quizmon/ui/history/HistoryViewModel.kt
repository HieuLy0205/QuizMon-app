package com.example.quizmon.ui.history

import android.app.Application
import androidx.lifecycle.*
import com.example.quizmon.data.model.Question
import com.example.quizmon.data.source.local.AppDatabase
import com.example.quizmon.data.source.local.HistoryRecord
import com.example.quizmon.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    //  Khởi tạo repository
    private val repository: HistoryRepository = run {
        val db = AppDatabase.getInstance(application)
        HistoryRepository(db.historyDao())
    }

    private val _selectedCategory   = MutableLiveData("all")
    private val _selectedAnswerType = MutableLiveData("all")
    val selectedCategory  : LiveData<String> = _selectedCategory
    val selectedAnswerType: LiveData<String> = _selectedAnswerType

    private val _filterTrigger = MutableLiveData(Pair("all", "all"))

    val historyList: LiveData<List<HistoryRecord>> = _filterTrigger.switchMap { (cat, ans) ->
        repository.getFiltered(cat, ans)
    }

    private val _categories = MutableLiveData<List<String>>(emptyList())
    val categories: LiveData<List<String>> = _categories

    val isFiltered: Boolean
        get() = _selectedCategory.value != "all" || _selectedAnswerType.value != "all"

    init {
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