package com.example.quizmon.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.data.repository.StatisticsRepository
import com.example.quizmon.utils.ChartHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsFragment : Fragment() {

    private lateinit var repository: StatisticsRepository
    private lateinit var tvTotalCorrect: TextView
    private lateinit var tvTotalWrong: TextView
    private lateinit var tvTotalQuestions: TextView
    private lateinit var tvCurrentStreak: TextView
    private lateinit var tvLongestStreak: TextView
    private lateinit var chartView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatisticsAdapter

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
        tvTotalCorrect = view.findViewById(R.id.tv_total_correct)
        tvTotalWrong = view.findViewById(R.id.tv_total_wrong)
        tvTotalQuestions = view.findViewById(R.id.tv_total_questions)
        tvCurrentStreak = view.findViewById(R.id.tv_current_streak)
        tvLongestStreak = view.findViewById(R.id.tv_longest_streak)
        chartView = view.findViewById(R.id.chart_view)
        recyclerView = view.findViewById(R.id.recycler_view_daily_stats)

        adapter = StatisticsAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val overall = withContext(Dispatchers.IO) {
                repository.getOverallStatistics()
            }

            val last7Days = withContext(Dispatchers.IO) {
                repository.getLast7DaysStats()
            }

            updateUI(overall, last7Days)
        }
    }

    private fun updateUI(overall: com.example.quizmon.data.model.OverallStatistics, last7Days: List<com.example.quizmon.data.model.Statistics>) {
        tvTotalCorrect.text = "Đúng: ${overall.totalCorrect}"
        tvTotalWrong.text = "Sai: ${overall.totalWrong}"
        tvTotalQuestions.text = "Tổng: ${overall.totalQuestions}"
        tvCurrentStreak.text = "🔥 Chuỗi hiện tại: ${overall.currentStreak} ngày"
        tvLongestStreak.text = "🏆 Chuỗi dài nhất: ${overall.longestStreak} ngày"

        // Vẽ biểu đồ
        val bitmapDrawable = ChartHelper.createBarChart(last7Days, 800, 400)
        chartView.background = bitmapDrawable

        // Cập nhật danh sách daily stats
        adapter.updateData(last7Days)
    }

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}