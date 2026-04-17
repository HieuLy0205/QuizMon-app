package com.example.quizmon.ui.pet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.quizmon.R
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import android.view.View
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.ui.shop.PreferenceManager


class PetActivity : AppCompatActivity() {

    //Chương trình hoạt ảnh gồm 3 thành phần
    private lateinit var imgPet1: ImageView
    //    1 (Dử liệu) chủng bị ảnh = danh sách ảnh ->
    private var petFarm = intArrayOf(
        R.drawable.dragon_pet_2,
        R.drawable.dragon_pet_1,
    )
//    2 (hành động) lấy ảnh hiện tại, hiển thị ảnh lên màn hình, chuẩn bị ảnh tiếp theo.
    private var currentPet = 0
    private var runnable = object : Runnable {
        override fun run() {
            imgPet1.setImageResource(petFarm[currentPet])
//            currentPet = (currentPet + 1) % petFarm.size
            val delayTime = if (currentPet == 0) 3000L else 200L
            currentPet = (currentPet + 1) % petFarm.size
            handle.postDelayed(this, delayTime)
            Log.d("PetAnim", "Frame: $currentPet - Delay: $delayTime")
//            handle.postDelayed(this, 5000)
//            Log.i("info", "ImageView tapped")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)
        imgPet1 = findViewById(R.id.imgPet1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
        startAnimation()
    }
    //3 (nhịp điêu) báo cáo với handl : định nghĩa thời gian chuyển ảnh
    private val handle = Handler(Looper.getMainLooper())
    private fun startAnimation() {
        handle.postDelayed(runnable, 150)
    }

    //HÀM TĂNG CONE
    override fun onResume() {
        super.onResume()
        updateCoinDisplay()
    }
    private fun updateCoinDisplay(){
        val textcoin = findViewById<TextView>(R.id.textcoin)
        val preferenceManager = PreferenceManager(this)
        textcoin.text = preferenceManager.getCoins().toString()
    }

}