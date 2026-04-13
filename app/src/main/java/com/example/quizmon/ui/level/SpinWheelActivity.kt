package com.example.quizmon.ui.level

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R
import kotlin.random.Random

class SpinWheelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_wheel)

        val ivWheel = findViewById<ImageView>(R.id.ivWheel)
        val btnSpin = findViewById<Button>(R.id.btnSpin)

        btnSpin.setOnClickListener {
            val degrees = Random.nextInt(3600) + 720 // Quay ít nhất 2 vòng
            val rotate = RotateAnimation(
                0f, degrees.toFloat(),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 3000
                fillAfter = true
            }

            rotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    btnSpin.isEnabled = false
                }

                override fun onAnimationEnd(animation: Animation?) {
                    val actualDegrees = degrees % 360
                    val result = if (actualDegrees in 0..180) "Cộng 50 điểm" else "Trừ 20 điểm"
                    Toast.makeText(this@SpinWheelActivity, "Kết quả: $result", Toast.LENGTH_SHORT).show()
                    
                    android.os.Handler(mainLooper).postDelayed({ finish() }, 1500)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            ivWheel.startAnimation(rotate)
        }
    }
}
