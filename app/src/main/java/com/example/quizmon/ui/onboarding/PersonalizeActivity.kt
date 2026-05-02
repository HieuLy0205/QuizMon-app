package com.example.quizmon.ui.onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R

class PersonalizeActivity : AppCompatActivity() {

    private var selectedGender = ""
    private val selectedTopics = mutableSetOf<String>()

    private lateinit var btnNext: Button
    private lateinit var edtCustomTopic: EditText
    private lateinit var customContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personalize)

        val prefs = getSharedPreferences("QuizMonPrefs", MODE_PRIVATE)

        val btnMale = findViewById<Button>(R.id.btnMale)
        val btnFemale = findViewById<Button>(R.id.btnFemale)
        val btnOther = findViewById<Button>(R.id.btnOther)

        btnNext = findViewById(R.id.btnNext)
        edtCustomTopic = findViewById(R.id.edtCustomTopic)
        customContainer = findViewById(R.id.customContainer)

        btnNext.isEnabled = false
        btnNext.alpha = 0.5f

        fun selectGender(g: String) {
            selectedGender = g
            btnMale.isSelected = g == "Nam"
            btnFemale.isSelected = g == "Nữ"
            btnOther.isSelected = g == "Khác"
            updateNextButton()
        }

        btnMale.setOnClickListener { selectGender("Nam") }
        btnFemale.setOnClickListener { selectGender("Nữ") }
        btnOther.setOnClickListener { selectGender("Khác") }

        setupTopic(R.id.topic_phim, "Phim ảnh", R.drawable.ic_movie)
        setupTopic(R.id.topic_xahoi, "Xã hội", R.drawable.ic_people)
        setupTopic(R.id.topic_lichsu, "Lịch sử", R.drawable.ic_history)
        setupTopic(R.id.topic_thethao, "Thể thao", R.drawable.ic_sport)
        setupTopic(R.id.topic_dialy, "Địa lý", R.drawable.ic_globe)
        setupTopic(R.id.topic_khoahoc, "Khoa học", R.drawable.ic_science)
        setupTopic(R.id.topic_tunhien, "Tự nhiên", R.drawable.ic_leaf)
        setupTopic(R.id.topic_vanhoa, "Văn hóa", R.drawable.ic_theater)

        edtCustomTopic.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCustomTopicFromInput()
                true
            } else {
                false
            }
        }

        btnNext.setOnClickListener {
            addCustomTopicFromInput()

            if (selectedGender.isBlank()) {
                Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedTopics.size < 3) {
                Toast.makeText(this, "Chọn ít nhất 3 chủ đề", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
            prefs.edit()
                .putString("gender", selectedGender)
                .putStringSet("topics", selectedTopics)
                .putBoolean("FIRST_TIME", false)
                .apply()
            startActivity(Intent(this, NicknameActivity::class.java))
            finish()
        }
    }
    private fun setupTopic(id: Int, value: String, iconRes: Int) {
        val layout = findViewById<View>(id)
        val text = layout.findViewById<TextView>(R.id.txtTopic)
        val icon = layout.findViewById<ImageView>(R.id.icon)
        val tick = layout.findViewById<ImageView>(R.id.tick)

        text.text = value
        icon.setImageResource(iconRes)
        tick.visibility = View.GONE

        layout.setOnClickListener {
            if (selectedTopics.contains(value)) {
                selectedTopics.remove(value)
                layout.isSelected = false
                tick.visibility = View.GONE
                text.setTextColor(Color.parseColor("#1F1F1F"))
            } else {
                selectedTopics.add(value)
                layout.isSelected = true
                tick.visibility = View.VISIBLE
                text.setTextColor(Color.WHITE)
            }

            updateNextButton()
        }
    }

    private fun addCustomTopicFromInput() {
        val text = edtCustomTopic.text.toString().trim()

        if (text.isBlank()) return
        if (selectedTopics.contains(text)) {
            edtCustomTopic.text.clear()
            updateNextButton()
            return
        }

        selectedTopics.add(text)
        addCustomChip(text)
        edtCustomTopic.text.clear()
        updateNextButton()
    }

    private fun addCustomChip(text: String) {
        val chip = TextView(this).apply {
            this.text = "$text  ×"
            setPadding(22, 8, 22, 8)
            setTextColor(Color.WHITE)
            textSize = 12f
            setBackgroundResource(R.drawable.bg_chip)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(6, 6, 6, 6)
        chip.layoutParams = params

        chip.setOnClickListener {
            selectedTopics.remove(text)
            customContainer.removeView(chip)
            updateNextButton()
        }

        customContainer.addView(chip)
    }

    private fun updateNextButton() {
        val enabled = selectedGender.isNotBlank() && selectedTopics.size >= 3
        btnNext.isEnabled = enabled
        btnNext.alpha = if (enabled) 1f else 0.5f
    }
}