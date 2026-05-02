package com.example.quizmon.ui.shop
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.quizmon.utils.TaskHeadManager
import com.example.quizmon.R
class shop_tim: AppCompatActivity() {
    private lateinit var pref: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_tim)
        pref = PreferenceManager(this)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.layout_taskhead), pref)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
}