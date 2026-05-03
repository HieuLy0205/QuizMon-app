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

class shop_tim : AppCompatActivity() {
    private lateinit var pref: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_tim)
        
        pref = PreferenceManager(this)
        
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        // Mua bằng Sao Ước (5 Sao = 1 Tim)
        findViewById<MaterialButton>(R.id.btnBuyWithStar).setOnClickListener {
            SoundManager.playClick()
            if (pref.getHearts() >= 5) {
                Toast.makeText(this, "Máu đã đầy!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pref.getCoins() >= 5) {
                pref.addCoin(-5)
                pref.addHearts(1)
                SoundManager.playCoin()
                Toast.makeText(this, "Đã mua 1 Tim bằng 5 Sao Ước", Toast.LENGTH_SHORT).show()
                updateHeader()
            } else {
                SoundManager.playWrong()
                Toast.makeText(this, "Không đủ Sao Ước!", Toast.LENGTH_SHORT).show()
            }
        }

        // Mua bằng Xu Cỏ (100 Xu = 1 Tim)
        findViewById<MaterialButton>(R.id.btnBuyWithXu).setOnClickListener {
            SoundManager.playClick()
            if (pref.getHearts() >= 5) {
                Toast.makeText(this, "Máu đã đầy!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pref.getXu() >= 100) {
                pref.addXu(-100)
                pref.addHearts(1)
                SoundManager.playCoin()
                Toast.makeText(this, "Đã mua 1 Tim bằng 100 Xu Cỏ", Toast.LENGTH_SHORT).show()
                updateHeader()
            } else {
                SoundManager.playWrong()
                Toast.makeText(this, "Không đủ Xu Cỏ!", Toast.LENGTH_SHORT).show()
            }
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
