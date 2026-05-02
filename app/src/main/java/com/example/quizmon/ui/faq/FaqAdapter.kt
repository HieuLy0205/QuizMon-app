package com.example.quizmon.ui.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R

class FaqAdapter(
    private val items: List<FaqItem>,
    private val onClick: (FaqItem) -> Unit
) : RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    // ViewHolder
    class FaqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }

    // Tạo view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq, parent, false)
        return FaqViewHolder(view)
    }

    // Gán dữ liệu
    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    // Số lượng item
    override fun getItemCount(): Int = items.size
}