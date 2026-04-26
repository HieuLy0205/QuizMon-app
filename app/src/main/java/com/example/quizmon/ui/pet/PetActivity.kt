package com.example.quizmon.ui.pet

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
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.data.repository.petReposiroty


class PetActivity : AppCompatActivity() {
    private lateinit var  pref: PreferenceManager
    val repotory = petReposiroty()
    private lateinit var petAnimetor: AnimetorActivity
    private lateinit var  imgPet1: ImageView
    private val currentPet =  0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        imgPet1 = findViewById(R.id.imgPet1)
        petAnimetor = AnimetorActivity(imgPet1)
        val btn_tanglevel = findViewById<Button>(R.id.btn_tanglevel)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        pref = PreferenceManager(this)
        btn_tanglevel.setOnClickListener {
            val currentid = pref.getPetLevel()
            val currientCoin = pref.getCoins()
            if (currentid < 3) {
                if (currientCoin >= 20) {
                    //mở bộ nhớ ra và
                    pref.saveCoins(currientCoin - 20)
                    val nextLevel = currentid + 1
                    pref.savePetLevel(nextLevel)
                    onResume()
                    updateHeaderStats()
                    Toast.makeText(
                        this,
                        "Tăng cấp Thành công", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Chưa đủ coin", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Toast.makeText(
                this,
                "không đạt yêu cầu", Toast.LENGTH_SHORT
            ).show()
        }
        btnBack.setOnClickListener { finish() }
    }

    //note 4: HÀM TĂNG CONE :onResume và updateConDisplay
    override fun onResume() {
        super.onResume()
        //hàm làm mới thông tin pet
        infomationPet()
        updateHeaderStats()
    }
    fun infomationPet(){
        val currentPetid = pref.getPetid()
        val currentlevel = pref.getPetLevel()
        val petdetail = repotory.getPetById(currentPetid.toString())
        petdetail?.let{
            val activypet = it.copy(currentelevel = currentlevel)
            petAnimetor.starAnimetor(activypet)
        }
    }
    // Đồng bộ với ID mới từ layout_taskhead
    private fun updateHeaderStats(){
        val preferenceManager = PreferenceManager(this)
        val textcoin = findViewById<TextView>(R.id.textcoins)
        textcoin.text=preferenceManager.getCoins().toString()
      }
}
