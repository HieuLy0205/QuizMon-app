package com.example.quizmon.ui.payment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.view.LayoutInflater
import com.example.quizmon.R

//lớp dữ liệu cho từng ô nạp tiên
class PaymentItem(
    val image: Int, //lưu trữ hình ảnh nạp
    val text: String, //lưu trữ tên nạp
    val amount: Int //lưu trử giá trị nạp
)
//lớp adapter cho thanh toán
class PaymentAdapter(
    private val list: List<PaymentItem>,
    private val onItemClick: (PaymentItem) -> Unit
) : RecyclerView.Adapter<PaymentAdapter.ViewHolder>(){

    //lớp dũ liệu các view trong một ô
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_infomation_payment, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.imageView.setImageResource(item.image)
        holder.itemView.setOnClickListener {
            onItemClick(item) }
    }
    override fun getItemCount(): Int {
        return list.size
    }
}
