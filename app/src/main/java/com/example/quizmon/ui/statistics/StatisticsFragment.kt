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

/**
 * Fragment hiển thị thống kê người dùng.
 * Đã fix lỗi nhấp nháy con số khi chuyển tab.
 */
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
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = StatisticsRepository(requireContext())

        initViews(view)
        
        // Trạng thái chờ trước khi nạp dữ liệu để tránh nhấp nháy
        clearUI()
        
        loadStatistics()
    }

    private fun initViews(view: View) {
        tvLevelsValue = view.findViewById(R.id.tv_levels_value)
        tvSinceValue = view.findViewById(R.id.tv_since_value)
        tvQuestionsValue = view.findViewById(R.id.tv_questions_value)
        tvRateValue = view.findViewById(R.id.tv_rate_value)

        view.findViewById<View>(R.id.btnBack)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Xóa các con số mặc định trong XML để tránh bị nháy giá trị cũ/mới
     */
    private fun clearUI() {
        tvLevelsValue.text = "--"
        tvSinceValue.text = "--"
        tvQuestionsValue.text = "--"
        tvRateValue.text = "--%"
    }

    private fun loadStatistics() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Chỉ định rõ kiểu dữ liệu <OverallStatistics>
                val overall = withContext<OverallStatistics>(Dispatchers.IO) {
                    repository.getOverallStatistics()
                }
                // Cập nhật giao diện một lần duy nhất khi dữ liệu đã sẵn sàng
                updateUI(overall)
            } catch (e: Exception) {
                e.printStackTrace()
                updateUI(OverallStatistics())
            }
        }
    }

    private fun updateUI(overall: OverallStatistics) {
        // Cập nhật số màn chơi
        tvLevelsValue.text = String.format(Locale.getDefault(), "%d", overall.levelsCompleted)

        // Định dạng ngày tham gia
        val vietnameseLocale = Locale("vi", "VN")
        val dateFormat = SimpleDateFormat("'tháng' M, yyyy", vietnameseLocale)
        val joinDate = overall.startDate ?: Date()
        tvSinceValue.text = dateFormat.format(joinDate)

        // Cập nhật tổng câu hỏi
        tvQuestionsValue.text = String.format(Locale.getDefault(), "%d", overall.totalQuestions)
        
        // Cập nhật tỷ lệ chính xác
        tvRateValue.text = String.format(Locale.getDefault(), "%d%%", overall.overallAccuracy)
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}
