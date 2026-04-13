package com.example.quizmon

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.ImageView
import android.view.View
import android.util.Log
import android.widget.Button
import kotlin.jvm.java
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.ui.shop.shop_phobien
import com.example.quizmon.ui.shop.shop_tim
import com.example.quizmon.ui.shop.shop_xu

class activity_shop : AppCompatActivity() {
//    private var isPet1Visible = true
//    private lateinit var btn_goi_api1: Button
//    private lateinit var btn_goi_api3: Button
//    private lateinit var btn_goi_api2: Button
//    private lateinit var btn_goi_api4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        setContentView(R.layout.activity_pet)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pet)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        findViewById<ImageView>(R.id.imgPet1).alpha = 1f
//        findViewById<ImageView>(R.id.imgPet2).alpha = 0f
//        findViewById<ImageView>(R.id.imgPet3).alpha = 1f
//        findViewById<ImageView>(R.id.imgPet4).alpha = 0f
        //Bắt đầu nút Rương mạng
        val btn_goi_api1 = findViewById<Button>(R.id.btn_goi_api1)
        btn_goi_api1.setOnClickListener {
            val intent = Intent(this, shop_tim::class.java)
            startActivity(intent)
        }
        val btn_goi_api2 = findViewById<Button>(R.id.btn_goi_api2)
        //Bắt đầu nút Rương xu
        btn_goi_api2.setOnClickListener {
            val intent = Intent(this, shop_xu::class.java)
            startActivity(intent)
        }
        val btn_goi_api3 = findViewById<Button>(R.id.btn_goi_api3)
        btn_goi_api3.setOnClickListener {
            val intent = Intent(this, shop_phobien::class.java)
            startActivity(intent)
        }
        val btn_goi_api4 = findViewById<Button>(R.id.btn_goi_api4)
        btn_goi_api4.setOnClickListener {
            val intent = Intent(this, shop_phobien::class.java)
            startActivity(intent)
        }
    }
//    fun fade(view: View) {
//        Log.i("info", "ImageView tapped")
//        val imgPet1 = findViewById<ImageView>(R.id.imgPet1)
//        val imgPet2 = findViewById<ImageView>(R.id.imgPet2)
//        val imgPet3 = findViewById<ImageView>(R.id.imgPet3)
//        val imgPet4 = findViewById<ImageView>(R.id.imgPet4)
//
//        if(isPet1Visible){
//            isPet1Visible = false
//            imgPet1.animate().alpha(0f).setDuration(500)
//            imgPet2.animate().alpha(1f).setDuration(500)
//            imgPet3.animate().alpha(0f).setDuration(500)
//            imgPet4.animate().alpha(1f).setDuration(500)
//
//        }else{
//            isPet1Visible = true
//            imgPet1.animate().alpha(1f).setDuration(500)
//            imgPet2.animate().alpha(0f).setDuration(500)
//            imgPet3.animate().alpha(0f).setDuration(500)
//        }
//    }

}