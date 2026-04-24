package com.example.quizmon.ui.shop
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.ui.payment.PaymentAdapter
import com.example.quizmon.ui.payment.PaymentItem
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import coil.load
import com.example.quizmon.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.button.MaterialButton

class shop_xu: AppCompatActivity() {

    private lateinit var imgQr: ImageView
    private  var selectedItem: PaymentItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_xu)
        val recyclerView = findViewById<RecyclerView>(R.id.rlvmenhgia)
        val imgQr = findViewById<ImageView>(R.id.imgQR)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnConfirmPaymenttn = findViewById<MaterialButton>(R.id.btnConfirmPayment)
        val preferenceManager = PreferenceManager(this)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val listpayment = listOf(
            PaymentItem(R.drawable.icon_payment_10, "10đ", 20),
            PaymentItem(R.drawable.icon_payment_20, "20đ", 50),
            PaymentItem(R.drawable.icon_payment_50,"50đ", 110),
            PaymentItem(R.drawable.icon_payment_100,"100đ", 240),
            PaymentItem(R.drawable.icon_payment_500,"500đ", 1200),
        )
        val adapter = PaymentAdapter(listpayment) { item ->
            selectedItem = item
            // Gọi hàm xử lý hiện QR
            SetupQr()
            showPaymentDialog(item)
        }
        recyclerView.adapter = adapter
        btnBack.setOnClickListener { finish() }
        btnConfirmPaymenttn.setOnClickListener {
            if (selectedItem == null) {
                Toast.makeText(this, "chọn loại thẻ thanh toán", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else
                preferenceManager.addXu(selectedItem!!.amount)
                Toast.makeText(this, "thanh toán thành công ${selectedItem!!.amount}đ", Toast.LENGTH_SHORT).show()
        }
    }
    private fun SetupQr() {
        val imgQr = findViewById<ImageView>(R.id.imgQR)
        val item = selectedItem ?: return
        //Tạo link thay số tài khoản
        val qrLink = "https://img.vietqr.io/image/MB-123456789-compact.png?amount=${item.amount}&addInfo=NAPXU_ID123"
        imgQr.load(qrLink)
    }
    private fun showPaymentDialog(item: PaymentItem) {
        Toast.makeText(this, "Bạn chọn nạp: ${item.text}", Toast.LENGTH_SHORT).show()
    }
}