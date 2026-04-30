package com.example.quizmon.ui.level

import android.graphics.Color
import android.view.*
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
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubMapViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_map, parent, false)
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

            val iconRes = when (item.status) {

                CompletionStatus.CORRECT -> R.drawable.bt_dung
                CompletionStatus.INCORRECT -> R.drawable.bt_sai
                CompletionStatus.BONUS -> R.drawable.bt_bonus

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

            ivIcon.setImageResource(iconRes)

            cardItem.setCardBackgroundColor(Color.TRANSPARENT)
            cardItem.cardElevation = 0f
            cardItem.radius = 0f

            //Không cho click lại
            itemView.isEnabled = item.status == CompletionStatus.NOT_STARTED
            itemView.alpha = if (item.status == CompletionStatus.NOT_STARTED) 1f else 0.6f

            itemView.setOnClickListener {
                if (item.status == CompletionStatus.NOT_STARTED) {
                    onItemClick(item, adapterPosition)
                }
            }
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