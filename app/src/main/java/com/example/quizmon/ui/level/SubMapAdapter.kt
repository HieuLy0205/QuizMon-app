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
            val density = itemView.context.resources.displayMetrics.density

            // 1. Lựa chọn Icon dựa trên trạng thái (Dùng bt_dung, bt_sai, bt_bonus)
            val iconRes = when (item.status) {
                CompletionStatus.CORRECT -> {
                    if (item.type == SubMapType.QUESTION) R.drawable.bt_dung
                    else R.drawable.bt_bonus
                }
                CompletionStatus.INCORRECT -> R.drawable.bt_sai
                CompletionStatus.NOT_STARTED -> {
                    when (item.type) {
                        SubMapType.QUESTION -> getCategoryIcon(item.category)
                        SubMapType.TREASURE -> R.drawable.bt_chest
                        SubMapType.SPIN_WHEEL -> R.drawable.bt_vqmm
                        SubMapType.FLIP_CARD -> R.drawable.bt_draw
                        else -> 0
                    }
                }
            }

            if (iconRes != 0) {
                ivIcon.setImageResource(iconRes)
            } else {
                ivIcon.setImageResource(0)
            }

            // 2. Xử lý hiển thị ô
            when (item.status) {
                CompletionStatus.CORRECT, CompletionStatus.INCORRECT -> {
                    // Khi đã trả lời, ảnh bt_dung/bt_sai/bt_bonus đã bao gồm cả hình dạng và màu sắc
                    cardItem.setCardBackgroundColor(Color.TRANSPARENT)
                    cardItem.cardElevation = 0f
                    cardItem.radius = 0f
                }

                CompletionStatus.NOT_STARTED -> {
                    if (item.type == SubMapType.QUESTION) {
                        cardItem.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        cardItem.radius = 12f * density
                        cardItem.cardElevation = 4f
                    } else {
                        cardItem.setCardBackgroundColor(Color.TRANSPARENT)
                        cardItem.cardElevation = 0f
                        cardItem.radius = 0f
                    }
                }
            }

            itemView.setOnClickListener { onItemClick(item, adapterPosition) }
        }

        private fun getCategoryIcon(category: String?): Int {
            return when (category) {
                "AmNhac" -> R.drawable.bt_an
                "ChoiChu" -> R.drawable.bt_cc
                "CNXHKH" -> R.drawable.bt_cnxhkh
                "DiaLy" -> R.drawable.bt_dia
                "DoVui" -> R.drawable.bt_dv
                "TiengAnh" -> R.drawable.bt_e
                "HoaHoc" -> R.drawable.bt_hoa
                "KienThucChung" -> R.drawable.bt_ktc
                "KinhTeChinhTri" -> R.drawable.bt_ktct
                "LichSu" -> R.drawable.bt_su
                "TinHoc" -> R.drawable.bt_tin
                "TuTuongHCM" -> R.drawable.bt_tthcm
                "VanHoc" -> R.drawable.bt_van
                "VatLy" -> R.drawable.bt_vl
                else -> R.drawable.ic_launcher_foreground
            }
        }
    }
}