package com.example.quizmon.ui.level

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R

class LevelAdapter(
    private val levels: List<Int>,
    private val currentLevel: Int,
    private val onLevelClick: (Int) -> Unit
) : RecyclerView.Adapter<LevelAdapter.LevelViewHolder>() {

    class LevelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvLevelNumber)
        val container: RelativeLayout = view.findViewById(R.id.levelNodeContainer)
        val ivLock: ImageView = view.findViewById(R.id.ivLock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_level_node, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levels[position]
        holder.tvNumber.text = level.toString()

        // Căn chỉnh ziczac (Trái - Giữa - Phải)
        val params = holder.container.layoutParams as FrameLayout.LayoutParams
        params.gravity = when (position % 4) {
            0 -> Gravity.CENTER_HORIZONTAL
            1 -> Gravity.START or Gravity.CENTER_VERTICAL
            2 -> Gravity.CENTER_HORIZONTAL
            else -> Gravity.END or Gravity.CENTER_VERTICAL
        }
        // Thêm margin để không sát mép màn hình
        val margin = 40 * holder.itemView.context.resources.displayMetrics.density
        if (params.gravity and Gravity.START == Gravity.START) params.marginStart = margin.toInt()
        if (params.gravity and Gravity.END == Gravity.END) params.marginEnd = margin.toInt()
        
        holder.container.layoutParams = params

        // Hiển thị trạng thái khóa/mở
        if (level <= currentLevel) {
            holder.container.setBackgroundResource(R.drawable.bg_map_node_unlocked)
            holder.ivLock.visibility = View.GONE
            holder.tvNumber.visibility = View.VISIBLE
            holder.itemView.setOnClickListener { onLevelClick(level) }
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_map_node_locked)
            holder.ivLock.visibility = View.VISIBLE
            holder.tvNumber.visibility = View.VISIBLE
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount() = levels.size
}