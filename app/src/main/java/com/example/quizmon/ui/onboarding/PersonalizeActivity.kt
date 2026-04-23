package com.example.quizmon.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R

class PersonalizeActivity : AppCompatActivity() {

    private var selectedGender = ""
    private val selectedTopics = mutableSetOf<String>()

    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personalize)

        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)

        val btnMale = findViewById<Button>(R.id.btnMale)
        val btnFemale = findViewById<Button>(R.id.btnFemale)
        val btnOther = findViewById<Button>(R.id.btnOther)
        btnNext = findViewById(R.id.btnNext)

        // disable lúc đầu
        btnNext.isEnabled = false
        btnNext.alpha = 0.5f

        // ===== GENDER =====
        fun selectGender(gender: String) {
            selectedGender = gender

            btnMale.isSelected = gender == "Nam"
            btnFemale.isSelected = gender == "Nữ"
            btnOther.isSelected = gender == "Khác"

            val selectedBtn = when (gender) {
                "Nam" -> btnMale
                "Nữ" -> btnFemale
                else -> btnOther
            }

            // animation nhẹ
            selectedBtn.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(120)
                .withEndAction {
                    selectedBtn.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
                .start()

            updateNextButton()
        }

        btnMale.setOnClickListener { selectGender("Nam") }
        btnFemale.setOnClickListener { selectGender("Nữ") }
        btnOther.setOnClickListener { selectGender("Khác") }

        // TOPICS
        setupTopic(R.id.topic_phim, "Phim ảnh", R.drawable.ic_movie)
        setupTopic(R.id.topic_xahoi, "Xã hội", R.drawable.ic_people)
        setupTopic(R.id.topic_lichsu, "Lịch sử", R.drawable.ic_history)
        setupTopic(R.id.topic_thethao, "Thể thao", R.drawable.ic_sport)
        setupTopic(R.id.topic_dialy, "Địa lý", R.drawable.ic_globe)
        setupTopic(R.id.topic_khoahoc, "Khoa học", R.drawable.ic_science)
        setupTopic(R.id.topic_tunhien, "Tự nhiên", R.drawable.ic_leaf)
        setupTopic(R.id.topic_vanhoa, "Văn hóa", R.drawable.ic_theater)

        //NEXT
        btnNext.setOnClickListener {

            if (selectedGender.isBlank()) {
                Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedTopics.size < 3) {
                Toast.makeText(this, "Chọn ít nhất 3 chủ đề", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString("gender", selectedGender)
                .putStringSet("topics", selectedTopics)
                .apply()

            startActivity(Intent(this, NicknameActivity::class.java))
            finish()
        }
    }

    //  UPDATE BUTTON
    private fun updateNextButton() {
        val enabled = selectedTopics.size >= 3 && selectedGender.isNotBlank()

        btnNext.isEnabled = enabled
        btnNext.alpha = if (enabled) 1f else 0.5f
    }

    //  TOPIC
    private fun setupTopic(layoutId: Int, value: String, iconRes: Int) {

        val layout = findViewById<View>(layoutId)
        val tick = layout.findViewById<ImageView>(R.id.tick)
        val text = layout.findViewById<TextView>(R.id.txtTopic)
        val icon = layout.findViewById<ImageView?>(R.id.icon)

        text.text = value
        icon?.setImageResource(iconRes)

        layout.setOnClickListener {

            // tránh bug animation
            layout.animate().cancel()

            if (selectedTopics.contains(value)) {
                selectedTopics.remove(value)
                tick.visibility = View.GONE
                layout.isSelected = false

                layout.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()

            } else {
                selectedTopics.add(value)
                tick.visibility = View.VISIBLE
                layout.isSelected = true

                layout.animate()
                    .scaleX(1.08f)
                    .scaleY(1.08f)
                    .setDuration(120)
                    .withEndAction {
                        layout.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(120)
                            .start()
                    }
                    .start()
            }

            updateNextButton()
        }
    }
}