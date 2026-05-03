package com.example.quizmon.ui.shop

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.TaskHeadManager
import com.google.android.material.button.MaterialButton

class shop_xu_buy : AppCompatActivity() {
    private lateinit var pref: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_xu_buy)

        pref = PreferenceManager(this)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        findViewById<MaterialButton>(R.id.btnBuy1).setOnClickListener {
            buyXu(10, 100)
        }

        findViewById<MaterialButton>(R.id.btnBuy2).setOnClickListener {
            buyXu(50, 550)
        }

        findViewById<MaterialButton>(R.id.btnBuy3).setOnClickListener {
            buyXu(100, 1200)
        }
    }

    private fun buyXu(starCost: Int, xuAmount: Int) {
        SoundManager.playClick()
        if (pref.getCoins() >= starCost) {
            pref.addCoin(-starCost)
            pref.addXu(xuAmount)
            SoundManager.playCoin()
            Toast.makeText(this, "Đã đổi $xuAmount Xu thành công!", Toast.LENGTH_SHORT).show()
            updateHeader()
        } else {
            SoundManager.playWrong()
            Toast.makeText(this, "Không đủ Sao Ước!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateHeader() {
        TaskHeadManager.update(findViewById(R.id.taskhead), pref)
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), pref)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
}
