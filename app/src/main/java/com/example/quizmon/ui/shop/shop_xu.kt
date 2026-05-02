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
import com.example.quizmon.utils.TaskHeadManager
import com.google.android.material.button.MaterialButton

class shop_xu: AppCompatActivity() {
    private  var selectedItem: PaymentItem? = null
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var btnBack: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnConfirmPaymenttn: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_xu)
        recyclerView = findViewById(R.id.rlvmenhgia)
        btnBack = findViewById(R.id.btnBack)
        btnConfirmPaymenttn = findViewById(R.id.btnConfirmPayment)
        preferenceManager = PreferenceManager(this)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        //khai báo list payment. để khai báo list này mình cần một fantion để khai báo
//        var list = listOf<Int>(1, 2, 3, 4, 5)
        val listpayment = listOf(
            PaymentItem(R.drawable.icon_payment_10, "10đ", 20),
            PaymentItem(R.drawable.icon_payment_20, "20đ", 50),
            PaymentItem(R.drawable.icon_payment_50,"50đ", 110),
            PaymentItem(R.drawable.icon_payment_100,"100đ", 240),
            PaymentItem(R.drawable.icon_payment_200,"200đ", 1200),
            PaymentItem(R.drawable.icon_payment_500,"500đ", 1200)
        )

        // bộ sử lý thứ nhất: nhấn vào ô danh sách RecyclerView hành động sẻ bắt qua Paymentadapter.
        val adapter = PaymentAdapter(listpayment) { item ->
            selectedItem = item
            // Gọi hàm xử lý hiện QR
            SetupQr()
            showPaymentDialog(item)
        }
        recyclerView.adapter = adapter

        btnBack.setOnClickListener { finish() }

        btnConfirmPaymenttn.setOnClickListener {
            val item = selectedItem
            if (selectedItem == null) {
                Toast.makeText(this, "chọn loại thẻ thanh toán", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                preferenceManager.addXu(item!!.amount)
                preferenceManager.Dk_batmo_xn("nv3", true)
                Toast.makeText(
                    this,
                    "thanh toán thành công ${selectedItem!!.amount}đ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.layout_taskhead), preferenceManager)
    }
    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
    //nói đến thanh toán: đây là bộ sử lý giao diện
    private fun SetupQr() {
        val imgQr = findViewById<ImageView>(R.id.imgQRpay)
        val imgQrmomo = findViewById<ImageView>(R.id.imgQR)
        val item = selectedItem ?: return
        //Tạo link thay số tài khoản
        val qrLink = "https://img.vietqr.io/image/ICB-0342061314-compact.png?amount=${item.amount}&addInfo=NAPXU_VNPAY_${item.text}"
        val qrLink1 = "https://img.vietqr.io/image/momo-0346541884-compact.png?amount=${item.amount}&addInfo=NAPXU_MOMO_${item.text}"
        imgQrmomo.setOnClickListener {
            imgQrmomo.load(qrLink1)
            Toast.makeText(this, "thanh toán QRmomo", Toast.LENGTH_SHORT).show()
        }
        imgQr.setOnClickListener {
            imgQr.load(qrLink)
            Toast.makeText(this, "thanh toán QRvnpay", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showPaymentDialog(item: PaymentItem) {
        Toast.makeText(this, "Bạn chọn nạp: ${item.text}", Toast.LENGTH_SHORT).show()
    }
}