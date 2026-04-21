package com.example.quizmon.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.quizmon.R
import com.example.quizmon.data.repository.StatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsFragment : Fragment() {

    private lateinit var repository: StatisticsRepository
    private lateinit var tvLevelsValue: TextView
    private lateinit var tvSinceValue: TextView
    private lateinit var tvQuestionsValue: TextView
    private lateinit var tvRateValue: TextView
    private lateinit var tvName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = StatisticsRepository(requireContext())

        initViews(view)
        loadStatistics()
    }

    private fun initViews(view: View) {
        tvLevelsValue = view.findViewById(R.id.tv_levels_value)
        tvSinceValue = view.findViewById(R.id.tv_since_value)
        tvQuestionsValue = view.findViewById(R.id.tv_questions_value)
        tvRateValue = view.findViewById(R.id.tv_rate_value)
        tvName = view.findViewById(R.id.tvName)
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val overall = withContext(Dispatchers.IO) {
                repository.getOverallStatistics()
            }
            updateUI(overall)
        }
    }

    private fun updateUI(overall: com.example.quizmon.data.model.OverallStatistics) {
        // Cập nhật dữ liệu từ repository vào giao diện mới
        tvLevelsValue.text = "${overall.totalCorrect / 5}" // Giả định logic tính level
        tvQuestionsValue.text = "${overall.totalQuestions}"
        
        val rate = if (overall.totalQuestions > 0) {
            (overall.totalCorrect * 100) / overall.totalQuestions
        } else 0
        tvRateValue.text = "$rate%"
        
        tvSinceValue.text = "Tháng 4 2024"
    }

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
