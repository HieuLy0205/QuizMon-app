package com.example.quizmon.ui.report

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.quizmon.R

class ReportDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_report, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        // CLICK ITEMS
        view.findViewById<android.view.View>(R.id.btnContent).setOnClickListener {
            openReport("Nội dung")
        }

        view.findViewById<android.view.View>(R.id.btnTech).setOnClickListener {
            openReport("Kỹ thuật")
        }

        view.findViewById<android.view.View>(R.id.btnAds).setOnClickListener {
            openReport("Quảng cáo")
        }

        view.findViewById<android.view.View>(R.id.btnPrivacy).setOnClickListener {
            openReport("Quyền riêng tư")
        }

        view.findViewById<android.view.View>(R.id.btnOther).setOnClickListener {
            openReport("Khác")
        }

        return dialog
    }

    private fun openReport(type: String) {
        val intent = Intent(requireContext(), ReportActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
        dismiss()
    }
}