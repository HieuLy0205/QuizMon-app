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
import android.widget.Toast
import android.view.View
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.ui.shop.PreferenceManager


class PetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        imgPet1 = findViewById(R.id.imgPet1)
        val btn_tanglevel = findViewById<Button>(R.id.btn_tanglevel)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btn_tanglevel.setOnClickListener {
            val currentLeve = pref.getPetLevel()
            val currientCoin = pref.getCoins()
            if(currentLeve<3){
                if(currientCoin>=20){
                    //mở bộ nhớ ra và
                    pref.saveCoins(currientCoin - 20)
                    val nextLevel = currentLeve + 1
                    pref.savePetLevel(nextLevel)
                    petFarm = getFramesByLevel(nextLevel)
                    currentPet = 0
                    updateHeaderStats()
                    Toast.makeText(this,
                        "Tăng cấp Thành công", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,
                        "Chưa đủ coin", Toast.LENGTH_SHORT).show()
                }
        }
            Toast.makeText(this,
                "không đạt yêu cầu", Toast.LENGTH_SHORT).show()
      }
        //note 1: sự kiện quy lại
        btnBack.setOnClickListener { finish() }
        startAnimation()
    }
    //note 3: Chương trình hoạt ảnh gồm 3 thành phần
    private lateinit var  imgPet1: ImageView

    private lateinit var pref: PreferenceManager
    //    1 (Dử liệu) chủng bị ảnh = danh sách ảnh ->
    private var petFarm = intArrayOf()

//    2 (hành động) lấy ảnh hiện tại, hiển thị ảnh lên màn hình, chuẩn bị ảnh tiếp theo.
    private var currentPet = 0
    private var runnable = object : Runnable {
        override fun run() {
            if (petFarm.isNotEmpty()) {
                imgPet1.setImageResource(petFarm[currentPet])
                val delayTime = if (currentPet == 0) 3000L else 200L
                currentPet = (currentPet + 1) % petFarm.size
                handle.postDelayed(this, delayTime)
                Log.d("PetAnim", "Frame: $currentPet - Delay: $delayTime")
            }
        }
    }
    //3 (nhịp điêu) báo cáo với handl : định nghĩa thời gian chuyển ảnh
    private val handle = Handler(Looper.getMainLooper())
    private fun startAnimation() {
        handle.postDelayed(runnable, 150)
    }

    //note 4: HÀM TĂNG CONE :onResume và updateConDisplay
    override fun onResume() {
        super.onResume()
        pref = PreferenceManager(this)
        val level = pref.getPetLevel()
        petFarm = getFramesByLevel(level)
        currentPet = 0
        updateHeaderStats()
    }
    
    // Đồng bộ với ID mới từ layout_taskhead
    private fun updateHeaderStats(){
        val preferenceManager = PreferenceManager(this)
        findViewById<TextView>(R.id.head_text_star)?.text = preferenceManager.getCoins().toString()
        findViewById<TextView>(R.id.head_text_coin)?.text = preferenceManager.getXu().toString()
    }
    
    private val FramesLevel1 = intArrayOf(R.drawable.dragon_c1_f1, R.drawable.dragon_c1_f2)
    private val FramesLevel2 = intArrayOf(R.drawable.dragon_c2_f1, R.drawable.dragon_c2_f2)
    private val FramesLevel3 = intArrayOf(R.drawable.dragon_pet_2, R.drawable.dragon_pet_1)
    private fun getFramesByLevel(level: Int): IntArray {
        return when (level) {
            1 -> FramesLevel1
            2 -> FramesLevel2
            3 -> FramesLevel3
            else -> FramesLevel1
        }
    }
}
