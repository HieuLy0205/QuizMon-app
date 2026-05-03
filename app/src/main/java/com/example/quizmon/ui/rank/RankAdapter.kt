package com.example.quizmon.ui.rank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R

class RankAdapter(private val list: List<RankItem>) : RecyclerView.Adapter<RankAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvExp: TextView = view.findViewById(R.id.tvLevel)
        val layoutItem: View = view.findViewById(R.id.layoutItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rank, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context
        
        holder.tvRank.text = item.rank.toString()
        holder.tvName.text = item.name
        holder.tvExp.text = context.getString(R.string.rank_exp_format, item.exp)

        val avatarRes = when (item.avatar) {
            "avatar1" -> R.drawable.avatar1
            "avatar2" -> R.drawable.avatar2
            "avatar_vip1" -> R.drawable.avatar_vip1
            else -> R.drawable.avatar1
        }
        holder.ivAvatar.setImageResource(avatarRes)

        if (item.isUser) {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_stats_bar)
            holder.layoutItem.backgroundTintList = ContextCompat.getColorStateList(context, R.color.taskbar_pink_bg)
        } else {
            holder.layoutItem.backgroundTintList = null
        }
    }

    override fun getItemCount() = list.size
}
