package com.example.quizmon.ui.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.data.model.Statistics
import java.text.SimpleDateFormat
import java.util.*

class StatisticsAdapter(private var statsList: List<Statistics>) :
    RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvCorrect: TextView = itemView.findViewById(R.id.tv_correct)
        val tvWrong: TextView = itemView.findViewById(R.id.tv_wrong)
        val tvTotal: TextView = itemView.findViewById(R.id.tv_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_stats, parent, false)
        return StatisticsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val stats = statsList[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.tvDate.text = dateFormat.format(stats.date)
        holder.tvCorrect.text = "✅ Đúng: ${stats.correctAnswers}"
        holder.tvWrong.text = "❌ Sai: ${stats.wrongAnswers}"
        holder.tvTotal.text = "📊 Tổng: ${stats.totalQuestions}"
    }

    override fun getItemCount(): Int = statsList.size

    fun updateData(newList: List<Statistics>) {
        statsList = newList
        notifyDataSetChanged()
    }
}