package com.example.quizmon.ui.shop
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.ui.payment.PaymentAdapter
import com.example.quizmon.ui.payment.PaymentItem
import android.os.Bundle
import android.widget.ImageButton
import com.example.quizmon.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager

class shop_xu: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_xu)

        val recyclerView = findViewById<RecyclerView>(R.id.rlvmenhgia)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val listpayment = listOf(
            PaymentItem(R.drawable.icon_payment_10, "10đ", 1),
            PaymentItem(R.drawable.icon_payment_20, "20đ", 2),
            PaymentItem(R.drawable.icon_payment_50,"50đ", 3),
            PaymentItem(R.drawable.icon_payment_100,"10đ", 4),
            PaymentItem(R.drawable.icon_payment_500,"500đ", 6),
        )
        val adapter = PaymentAdapter(listpayment) { item ->
            // Gọi hàm xử lý hiện QR
            showPaymentDialog(item)
        }
        recyclerView.adapter = adapter

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }

    private fun showPaymentDialog(item: PaymentItem) {
        android.widget.Toast.makeText(this, "Bạn chọn nạp: ${item.text}", android.widget.Toast.LENGTH_SHORT).show()
    }
}