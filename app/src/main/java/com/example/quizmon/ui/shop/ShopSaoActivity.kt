package com.example.quizmon.ui.shop

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.quizmon.R
import com.example.quizmon.ui.payment.PaymentAdapter
import com.example.quizmon.ui.payment.PaymentItem
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager
import com.google.android.material.button.MaterialButton

class ShopSaoActivity : AppCompatActivity() {
    private var selectedItem: PaymentItem? = null
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var btnBack: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnConfirmPayment: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_sao)

        recyclerView = findViewById(R.id.rlvmenhgia)
        btnBack = findViewById(R.id.btnBack)
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment)
        preferenceManager = PreferenceManager(this)

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val listPayment = listOf(
            PaymentItem(R.drawable.icon_payment_10, "10đ", 20),
            PaymentItem(R.drawable.icon_payment_20, "20đ", 50),
            PaymentItem(R.drawable.icon_payment_50, "50đ", 110),
            PaymentItem(R.drawable.icon_payment_100, "100đ", 240),
            PaymentItem(R.drawable.icon_payment_200, "200đ", 500),
            PaymentItem(R.drawable.icon_payment_500, "500đ", 1500)
        )

        val adapter = PaymentAdapter(listPayment) { item ->
            selectedItem = item
            setupQr()
            showPaymentDialog(item)
        }
        recyclerView.adapter = adapter

        btnBack.setOnClickListener { finish() }

        btnConfirmPayment.setOnClickListener {
            val item = selectedItem
            if (item == null) {
                Toast.makeText(this, "Vui lòng chọn mệnh giá nạp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                preferenceManager.addCoin(item.amount)
                preferenceManager.Dk_batmo_xn("nv3", true)
                Toast.makeText(
                    this,
                    "Nạp thành công ${item.amount} Sao ước!",
                    Toast.LENGTH_SHORT
                ).show()
                updateHeader()
            }
        }
    }

    private fun updateHeader() {
        TaskHeadManager.update(findViewById(R.id.taskhead), preferenceManager)
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }

    private fun setupQr() {
        val imgQrVnpay = findViewById<ImageView>(R.id.imgQRpay)
        val imgQrMomo = findViewById<ImageView>(R.id.imgQR)
        val item = selectedItem ?: return

        val qrVnpay = "https://img.vietqr.io/image/ICB-0342061314-compact.png?amount=${item.amount}&addInfo=NAPSAO_VNPAY_${item.text}"
        val qrMomo = "https://img.vietqr.io/image/momo-0346541884-compact.png?amount=${item.amount}&addInfo=NAPSAO_MOMO_${item.text}"

        imgQrMomo.setOnClickListener {
            imgQrMomo.load(qrMomo)
            Toast.makeText(this, "Đang tải mã QR MoMo...", Toast.LENGTH_SHORT).show()
        }
        imgQrVnpay.setOnClickListener {
            imgQrVnpay.load(qrVnpay)
            Toast.makeText(this, "Đang tải mã QR VNPay...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPaymentDialog(item: PaymentItem) {
        Toast.makeText(this, "Bạn chọn nạp gói: ${item.text}", Toast.LENGTH_SHORT).show()
    }
}
