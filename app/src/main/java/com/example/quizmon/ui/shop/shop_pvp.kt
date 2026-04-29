package com.example.quizmon.ui.shop
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.os.PersistableBundle
import android.widget.Button
import com.example.quizmon.R
import com.example.quizmon.ui.pet.AnimetorActivity

private lateinit var preferenceManager: PreferenceManager
private lateinit var  btnNhanPetFree: Button

class shop_pvp: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_pvp)

        val btnNhanPet1 = findViewById<Button>(R.id.btn_nhanpet1)
        val btnNhanPet2 = findViewById<Button>(R.id.btn_nhanpet2)
        val btnNhanPet3 = findViewById<Button>(R.id.btn_nhanpet3)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }
        setupnhanpet()

    }
    fun setupnhanpet(){
        preferenceManager = PreferenceManager(this)
        btnNhanPetFree = findViewById(R.id.btn_nhanpetFree)
        btnNhanPetFree.setOnClickListener {
            preferenceManager.addpetid(1)

        }
    }
}