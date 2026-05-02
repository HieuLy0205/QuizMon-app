package com.example.quizmon.ui.shop
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager
import kotlin.collections.remove


class shop_phobien: AppCompatActivity() {
    private lateinit var btn_Nv1: Button
    private lateinit var btn_Nv2: Button
    private lateinit var btn_Nv3: Button
    private lateinit var btn_Nv4: Button
    private lateinit var btn_Nv5: Button
    private lateinit var btnBack: ImageButton
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_phobien)
        btn_Nv1 = findViewById(R.id.btn_Nv1)
        btn_Nv2 = findViewById(R.id.btn_Nv2)
        btn_Nv3 = findViewById(R.id.btn_Nv3)
        btn_Nv4 = findViewById(R.id.btn_Nv4)
        btn_Nv5 = findViewById(R.id.btn_Nv5)
        btnBack = findViewById(R.id.btnBack)

        // kiểm tra khi vào màn hình thông qua preference
        preferenceManager = PreferenceManager(this)
        setup_nv_nhancoin()

        btnBack.setOnClickListener {
            // Xử lý khi nút được nhấn
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        //Tự động cập nhật Header và đếm ngược Tim
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
    }

    override fun onPause() {
        super.onPause()
        //Dừng cập nhật
        TaskHeadManager.stopLoop()
    }
    fun setup_nv_nhancoin(){
        val preferenceManager = PreferenceManager(this)
        if (preferenceManager.saver_va_inday("nv1")){
            btn_Nv1.isEnabled = false
            btn_Nv1.text = "Đã nhận hôm nay"
        }
        if (preferenceManager.saver_va_inday("nv2")){
            btn_Nv2.isEnabled = false
            btn_Nv2.text = "Đã nhận hôm nay"
        }
        if(preferenceManager.saver_va_inday("nv3")){
            btn_Nv3.isEnabled = false
            btn_Nv3.text = "Đã nhận hôm nay"
        }
        if(preferenceManager.saver_va_inday("nv4")){
            btn_Nv4.isEnabled = false
            btn_Nv4.text = "Đã nhận hôm nay"
        }
        if (preferenceManager.saver_va_inday("nv5")){
            btn_Nv5.isEnabled = false
            btn_Nv5.text = "Đã nhận hôm nay"
        }

        // sự kiện lick nhiệm vụ hằng ngày tăng được coin
        btn_Nv1.setOnClickListener {
            // Xử lý khi nút được nhấn
            preferenceManager.addCoin(10)
            preferenceManager.Xn_va_inday("nv1")
            btn_Nv1.isEnabled = false
            btn_Nv1.text = "Đã hoàn thành"
        }
        btn_Nv2.setOnClickListener {
            val isReady = preferenceManager.Dk_xacnhan_cq("nv2")
            val isReceivedToday = preferenceManager.saver_va_inday("nv2")
            // ! phủ định
            if (isReady && !isReceivedToday) {
                preferenceManager.addCoin(20)
                preferenceManager.Xn_va_inday("nv2")
                btn_Nv2.isEnabled = false
                btn_Nv2.text = "Đã hoàn thành"
                preferenceManager.Dk_batmo_xn("nv2", false)
            }else{
                Toast.makeText(this,
                    "Chưa xong ải", Toast.LENGTH_SHORT).show()
            }
        }
        btn_Nv3.setOnClickListener {
            val isReady = preferenceManager.Dk_xacnhan_cq("nv3")
            val isReceivedToday = preferenceManager.saver_va_inday("nv3")
            if (isReady && !isReceivedToday) {
                preferenceManager.addCoin(20)
                preferenceManager.Xn_va_inday("nv3")
                btn_Nv3.isEnabled = false
                btn_Nv3.text = "Đã hoàn thành"
                preferenceManager.Dk_batmo_xn("nv3", false)
            }else{
                Toast.makeText(this,
                    "bạn phải nạp xu", Toast.LENGTH_SHORT).show()
            }
        }
        btn_Nv4.setOnClickListener {
            val isReady = preferenceManager.Dk_xacnhan_cq("nv4")
            val isReceivedToday = preferenceManager.saver_va_inday("nv4")
            if (isReady && !isReceivedToday) {
                preferenceManager.addCoin(10)
                preferenceManager.Xn_va_inday("nv4")
                btn_Nv4.isEnabled = false
                btn_Nv4.text = "Đã hoàn thành"
                preferenceManager.Dk_batmo_xn("nv4", false)
            }else{
                Toast.makeText(this,
                    "bạn phải mua trứng trong shop pvp",
                    Toast.LENGTH_SHORT).show()
            }
        }
        btn_Nv5.setOnClickListener {
            val isReady = preferenceManager.Dk_xacnhan_cq("nv5")
            val isReceivedToday = preferenceManager.saver_va_inday("nv5")
            if (isReady && !isReceivedToday) {
                preferenceManager.addCoin(5)
                preferenceManager.Xn_va_inday("nv5")
                btn_Nv5.isEnabled = false
                btn_Nv5.text = "Đã hoàn thành"
                preferenceManager.Dk_batmo_xn("nv5", false)
            }else{
                Toast.makeText(this,
                    "bạn phải hoàn thành 1 câu hỏi trong trò chơi",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

}
