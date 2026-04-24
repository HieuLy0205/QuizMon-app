package com.example.quizmon.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.quizmon.R
import com.example.quizmon.data.model.OverallStatistics
import com.example.quizmon.data.repository.StatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var repository: StatisticsRepository
    private lateinit var tvLevelsValue: TextView
    private lateinit var tvSinceValue: TextView
    private lateinit var tvQuestionsValue: TextView
    private lateinit var tvRateValue: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sử dụng layout fragment_statistics đã được tối giản
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
    }

    private fun loadStatistics() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val overall = withContext(Dispatchers.IO) {
                    repository.getOverallStatistics()
                }
                updateUI(overall)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(overall: OverallStatistics) {
        tvLevelsValue.text = overall.levelsCompleted.toString()

        val dateFormat = SimpleDateFormat("MMMM, yyyy", Locale("vi", "VN"))
        val joinDate = overall.startDate ?: Date()
        tvSinceValue.text = dateFormat.format(joinDate)

        tvQuestionsValue.text = overall.totalQuestions.toString()
        tvRateValue.text = "${overall.overallAccuracy}%"
    }

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
