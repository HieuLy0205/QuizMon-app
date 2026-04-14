package com.example.quizmon.ui.shop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.R

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

}