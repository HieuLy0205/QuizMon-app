package com.example.quizmon.ui.report

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.R

class ReportActivity : AppCompatActivity() {

    private lateinit var etDescription: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report)

        etDescription = findViewById(R.id.etDescription)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener {
            handleSubmit()
        }
    }

    private fun handleSubmit() {
        val description = etDescription.text.toString().trim()
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (description.isEmpty()) {
            etDescription.error = "Vui lòng nhập nội dung"
            return
        }

        if (name.isEmpty()) {
            etName.error = "Vui lòng nhập tên"
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Vui lòng nhập email"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email không hợp lệ"
            return
        }

        Toast.makeText(this, "Đã gửi báo cáo thành công!", Toast.LENGTH_LONG).show()
        finish()
    }
}