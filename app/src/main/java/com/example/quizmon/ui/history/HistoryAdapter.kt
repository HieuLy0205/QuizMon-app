package com.example.quizmon.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.data.source.local.HistoryRecord
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter(
    private val onItemClick: (HistoryRecord) -> Unit
) : ListAdapter<HistoryAdapter.HistoryItem, RecyclerView.ViewHolder>(DiffCallback()) {

    sealed class HistoryItem {
        data class DateHeader(val label: String) : HistoryItem()
        data class RecordItem(val record: HistoryRecord) : HistoryItem()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_RECORD = 1

        private val sdfKey     = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        private val sdfDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

//     Chuyển List<HistoryRecord> → List<HistoryItem> (gắn DateHeader khi đổi ngày)
        fun buildItems(records: List<HistoryRecord>): List<HistoryItem> {
            val result  = mutableListOf<HistoryItem>()
            var lastKey = ""

            val today     = sdfKey.format(Date())
            val yesterday = sdfKey.format(Date(System.currentTimeMillis() - 86_400_000L))

            for (record in records) {
                val key   = sdfKey.format(Date(record.answeredAt))
                val disp  = sdfDisplay.format(Date(record.answeredAt))
                if (key != lastKey) {
                    val label = when (key) {
                        today     -> "Hôm nay — $disp"
                        yesterday -> "Hôm qua — $disp"
                        else      -> disp
                    }
                    result.add(HistoryItem.DateHeader(label))
                    lastKey = key
                }
                result.add(HistoryItem.RecordItem(record))
            }
            return result
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is HistoryItem.DateHeader -> TYPE_HEADER
        is HistoryItem.RecordItem -> TYPE_RECORD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderVH(inf.inflate(R.layout.item_history_date_header, parent, false))
            else        -> RecordVH(inf.inflate(R.layout.item_history, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HistoryItem.DateHeader -> (holder as HeaderVH).bind(item.label)
            is HistoryItem.RecordItem -> (holder as RecordVH).bind(item.record, onItemClick)
        }
    }

    // ── ViewHolder: Header ngày ───────────────────────────────────────────────
    class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv: TextView = view.findViewById(R.id.tv_date_header)
        fun bind(label: String) { tv.text = label }
    }

    // ── ViewHolder: Thẻ câu hỏi ──────────────────────────────────────────────
    class RecordVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCategory    : TextView = view.findViewById(R.id.tv_category)
        private val tvQuestion    : TextView = view.findViewById(R.id.tv_question)
        private val tvResult      : TextView = view.findViewById(R.id.tv_result)
        private val tvChosenAnswer: TextView = view.findViewById(R.id.tv_chosen_answer)

        fun bind(record: HistoryRecord, onClick: (HistoryRecord) -> Unit) {
            val ctx = itemView.context
            tvCategory.text = record.category
            tvQuestion.text = record.questionText

            if (record.isCorrect) {
                tvResult.text = "✓ Đúng"
                tvResult.setTextColor(ContextCompat.getColor(ctx, R.color.correct_green))
                tvResult.setBackgroundResource(R.drawable.bg_badge_correct)
            } else {
                tvResult.text = "✗ Sai"
                tvResult.setTextColor(ContextCompat.getColor(ctx, R.color.wrong_red))
                tvResult.setBackgroundResource(R.drawable.bg_badge_wrong)
            }

            val chosen = record.getOptions().getOrNull(record.chosenIndex) ?: ""
            tvChosenAnswer.text = "Đã chọn: $chosen"

            itemView.setOnClickListener { onClick(record) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(old: HistoryItem, new: HistoryItem) = when {
            old is HistoryItem.DateHeader && new is HistoryItem.DateHeader -> old.label == new.label
            old is HistoryItem.RecordItem && new is HistoryItem.RecordItem -> old.record.id == new.record.id
            else -> false
        }
        override fun areContentsTheSame(old: HistoryItem, new: HistoryItem) = old == new
    }
}