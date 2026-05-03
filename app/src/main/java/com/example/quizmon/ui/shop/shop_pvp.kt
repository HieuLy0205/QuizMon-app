package com.example.quizmon.ui.shop
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.os.PersistableBundle
import android.widget.Button
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.ui.pet.AnimetorActivity
import com.example.quizmon.ui.pet.TupetActivity
import com.example.quizmon.utils.TaskHeadManager

class shop_pvp: AppCompatActivity() {
    private lateinit var pref: PreferenceManager
    private lateinit var btnNhanPetFree: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_pvp)
        pref = PreferenceManager(this)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
        setupnhanpet()

    }

    fun setupnhanpet() {
        pref = PreferenceManager(this)
        btnNhanPetFree = findViewById(R.id.btn_nhanpetFree)

        if (pref.saver_va_inday("nh_trung_1")) {
            btnNhanPetFree.isEnabled = false
            btnNhanPetFree.text = "Đã nhận hôm nay"
        }

        btnNhanPetFree.setOnClickListener {
            // trứng tăng lên 1
            pref.add_sh_Egg("1")
            val isReady = pref.Dk_xacnhan_cq("nh_trung_1")
            val isReceivedToday = pref.saver_va_inday("nh_trung_1")
            if (isReady && !isReceivedToday) {
                pref.Xn_va_inday("nh_trung_1")
                btnNhanPetFree.isEnabled = false
                btnNhanPetFree.text = "Nhận ngay"
                pref.Dk_batmo_xn("nh_trung_1", false)
            }
        }

    }
    override fun onResume() {
        super.onResume()
        //Tự động cập nhật Header và đếm ngược Tim
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), pref)
    }

    override fun onPause() {
        super.onPause()
        //Dừng cập nhật
        TaskHeadManager.stopLoop()
    }

}