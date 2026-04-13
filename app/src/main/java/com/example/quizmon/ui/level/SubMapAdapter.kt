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
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = items.size

    inner class SubMapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardItem: CardView = itemView.findViewById(R.id.cardItem)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val tvId: TextView = itemView.findViewById(R.id.tvId)

        fun bind(item: SubMapItem, position: Int) {
            tvId.text = ""
            
            // Xử lý màu sắc và icon dựa trên trạng thái hoàn thành
            when (item.status) {
                CompletionStatus.CORRECT -> {
                    cardItem.setCardBackgroundColor(Color.parseColor("#4CAF50")) // Green
                    ivIcon.setImageResource(android.R.drawable.checkbox_on_background) // Placeholder cho dấu tick
                    ivIcon.setColorFilter(Color.WHITE)
                }
                CompletionStatus.INCORRECT -> {
                    cardItem.setCardBackgroundColor(Color.parseColor("#F44336")) // Red
                    ivIcon.setImageResource(android.R.drawable.ic_delete) // Placeholder cho dấu X
                    ivIcon.setColorFilter(Color.WHITE)
                }
                CompletionStatus.NOT_STARTED -> {
                    ivIcon.clearColorFilter()
                    val bgColor = when (item.type) {
                        SubMapType.QUESTION -> {
                            tvId.text = "1"
                            when (item.category) {
                                "CNKHXH" -> Color.parseColor("#FF80AB")
                                "DiaLy" -> Color.parseColor("#4FC3F7")
                                "Toan" -> Color.parseColor("#FFB74D")
                                else -> Color.parseColor("#AED581")
                            }
                        }
                        SubMapType.TREASURE -> Color.parseColor("#FFEB3B")
                        SubMapType.SPIN_WHEEL -> Color.parseColor("#BA68C8")
                        SubMapType.FLIP_CARD -> Color.parseColor("#FF7043")
                        else -> Color.WHITE
                    }
                    cardItem.setCardBackgroundColor(bgColor)

                    if (item.type == SubMapType.TREASURE) {
                        ivIcon.setImageResource(R.drawable.icon_ruong_xu)
                    } else if (item.type == SubMapType.QUESTION) {
                        ivIcon.setImageResource(R.drawable.ic_launcher_foreground)
                    } else {
                        ivIcon.setImageResource(0)
                    }
                }
            }

            itemView.setOnClickListener { onItemClick(item, position) }
        }
    }
}
