package com.example.quizmon.ui.shop
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.quizmon.R

class shop_phobien: AppCompatActivity() {
    private lateinit var btn_Nv1: Button
    private lateinit var btn_Nv2: Button
    private lateinit var btn_Nv3: Button
    private lateinit var btn_Nv4: Button
    private lateinit var btn_Nv5: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_phobien)
        btn_Nv1 = findViewById(R.id.btn_Nv1)
        btn_Nv2 = findViewById(R.id.btn_Nv2)
        btn_Nv3 = findViewById(R.id.btn_Nv3)
        btn_Nv4 = findViewById(R.id.btn_Nv4)
        btn_Nv5 = findViewById(R.id.btn_Nv5)

        btn_Nv1.setOnClickListener {
            // Xử lý khi nút được nhấn
        }



    }



}
