package com.example.quizmon.ui.level

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R

class SubMapAdapter(
    private var items: List<SubMapItem?>,
    private val onItemClick: (SubMapItem, Int) -> Unit
) : RecyclerView.Adapter<SubMapAdapter.SubMapViewHolder>() {

    fun updateData(newItems: List<SubMapItem?>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubMapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_map, parent, false)
        return SubMapViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubMapViewHolder, position: Int) {
        val item = items[position]
        if (item == null) {
            holder.itemView.visibility = View.INVISIBLE
            return
        }
        
        holder.itemView.visibility = View.VISIBLE
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class SubMapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardItem: CardView = itemView.findViewById(R.id.cardItem)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val tvId: TextView = itemView.findViewById(R.id.tvId)

        fun bind(item: SubMapItem) {
            tvId.text = ""
            ivIcon.clearColorFilter()
            
            // Xử lý icon dựa trên loại và chủ đề
            val iconRes = when (item.status) {
                CompletionStatus.CORRECT -> android.R.drawable.checkbox_on_background
                CompletionStatus.INCORRECT -> android.R.drawable.ic_delete
                CompletionStatus.NOT_STARTED -> {
                    when (item.type) {
                        SubMapType.QUESTION -> getCategoryIcon(item.category)
                        SubMapType.TREASURE -> R.drawable.icon_ruong_xu
                        else -> 0
                    }
                }
            }

            if (iconRes != 0) {
                ivIcon.setImageResource(iconRes)
                if (item.status != CompletionStatus.NOT_STARTED) {
                    ivIcon.setColorFilter(Color.WHITE)
                }
            } else {
                ivIcon.setImageResource(0)
            }

            // Xử lý màu nền - Hòa với nền khi chưa bắt đầu
            val bgColor = when (item.status) {
                CompletionStatus.CORRECT -> Color.parseColor("#4CAF50")
                CompletionStatus.INCORRECT -> Color.parseColor("#F44336")
                CompletionStatus.NOT_STARTED -> Color.TRANSPARENT
            }
            cardItem.setCardBackgroundColor(bgColor)
            cardItem.cardElevation = if (item.status == CompletionStatus.NOT_STARTED) 0f else 4f

            itemView.setOnClickListener { onItemClick(item, adapterPosition) }
        }

        private fun getCategoryIcon(category: String?): Int {
            return when (category) {
                "Hoa" -> R.drawable.bt_hoa
                "CNXHKH" -> R.drawable.bt_cnxhkh
                "DiaLy" -> R.drawable.bt_dia
                "VanHoc" -> R.drawable.bt_van
                "LichSu" -> R.drawable.bt_su
                "VatLy" -> R.drawable.bt_vl
                "AmNhac" -> R.drawable.bt_an
                else -> R.drawable.ic_launcher_foreground
            }
        }
    }
}
