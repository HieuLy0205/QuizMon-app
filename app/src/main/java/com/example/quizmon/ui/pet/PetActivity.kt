package com.example.quizmon.ui.pet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizmon.R
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.data.repository.petReposiroty
import com.example.quizmon.utils.TaskHeadManager


class PetActivity : AppCompatActivity() {
    private lateinit var  pref: PreferenceManager
    val repotory = petReposiroty()
    private lateinit var petAnimetor: AnimetorActivity
    private lateinit var  imgPet1: ImageView

    private lateinit var btn_tupet: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)
        pref = PreferenceManager(this)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btn_tupet = findViewById(R.id.btn_tupet)
        imgPet1 = findViewById(R.id.imgPet1)
        petAnimetor = AnimetorActivity(imgPet1)
        
        val btn_tanglevel = findViewById<Button>(R.id.btn_tanglevel)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        
        // Sự kiện tăng cấp pet với 3 mức giá khác nhau
        btn_tanglevel.setOnClickListener {
            val currentLevel = pref.getPetLevel()
            val currentCoins = pref.getCoins()

            // 1. Xác định giá nâng cấp dựa trên level hiện tại
            val muc_gia = when (currentLevel) {
                1 -> 20
                2 -> 50
                3 -> 80
                else -> -1
            }
            // 2. Kiểm tra điều kiện tối đa
            if (muc_gia == -1 || currentLevel >= 4) {
                Toast.makeText(this, "Pet đã đạt cấp tối đa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Kiểm tra tiền và thực hiện trừ coin + tăng cấp
            if (currentCoins >= muc_gia) {
                pref.saveCoins(currentCoins - muc_gia)
                val nextLevel = currentLevel + 1
                pref.savePetLevel(nextLevel)
                
                // Cập nhật giao diện
                infomationPet()
                TaskHeadManager.update(findViewById(R.id.taskhead), pref)

                Toast.makeText(this, "Nâng lên cấp $nextLevel thành công! -$muc_gia", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bạn cần $muc_gia coin để nâng cấp Hiện có: $currentCoins", Toast.LENGTH_SHORT).show()
            }
        }

        btn_tupet.setOnClickListener {
            startActivity(Intent(this, TupetActivity::class.java))
        }
        btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        infomationPet()
        //Kích hoạt đếm ngược Tim đồng bộ qua Manager
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), pref)
    }
    override fun onPause() {
        super.onPause()
        //Dừng đếm ngược và dừng hoạt ảnh pet để tiết kiệm tài nguyên
        TaskHeadManager.stopLoop()
        petAnimetor.stop()
    }
    fun infomationPet(){
        //mới xữa ẩn imgPet1
        if(pref.getPetid() == 0){
            imgPet1.visibility = android.view.View.INVISIBLE
            return
        }
        imgPet1.visibility = android.view.View.VISIBLE

        val currentPetid = pref.getPetid()
        val currentlevel = pref.getPetLevel()
        val petdetail = repotory.getPetById(currentPetid.toString())
        petdetail?.let{
            val activitypet = it.copy(currentelevel = currentlevel)
            petAnimetor.starAnimetor(activitypet)
        }
    }
}
