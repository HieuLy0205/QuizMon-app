package com.example.quizmon.ui.faq

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.R
import com.example.quizmon.ui.report.ReportActivity

class FaqActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        Log.d("FAQ", "FaqActivity opened")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerFaq)

        val faqList = listOf(
            FaqItem(
                "Cách chơi",
                "QuizMon là trò chơi trí tuệ giúp bạn rèn luyện kiến thức qua nhiều cấp độ khác nhau. " +
                        "Người chơi sẽ trả lời các câu hỏi trắc nghiệm để tích lũy điểm số và mở khóa các màn chơi mới.\n\n" +
                        "Mỗi câu trả lời đúng sẽ giúp bạn tiến xa hơn, trong khi trả lời sai có thể khiến bạn mất lượt hoặc phải chơi lại.\n\n" +
                        "Độ khó của câu hỏi sẽ tăng dần theo tiến trình, vì vậy hãy cố gắng duy trì chuỗi trả lời đúng để đạt điểm cao nhất."
            ),
            FaqItem(
                "Ứng dụng gặp vấn đề kỹ thuật",
                "Nếu bạn gặp lỗi như ứng dụng bị đứng, thoát đột ngột hoặc không tải được dữ liệu, hãy thử các bước sau:\n\n" +
                        "• Kiểm tra kết nối Internet\n" +
                        "• Khởi động lại ứng dụng\n" +
                        "• Cập nhật phiên bản mới nhất\n\n" +
                        "Nếu vấn đề vẫn tiếp tục, bạn có thể sử dụng chức năng 'Báo cáo vấn đề' để gửi thông tin đến đội ngũ hỗ trợ."
            ),
            FaqItem(
                "Tài khoản gặp vấn đề",
                "Nếu bạn không thể đăng nhập hoặc mất tài khoản, hãy kiểm tra lại thông tin đăng nhập của mình.\n\n" +
                        "Bạn cũng có thể sử dụng chức năng khôi phục mật khẩu thông qua email đã đăng ký.\n\n" +
                        "Trong trường hợp tài khoản bị khóa hoặc có dấu hiệu bất thường, vui lòng liên hệ hỗ trợ để được xử lý nhanh chóng."
            ),
            FaqItem(
                "Điểm và cuộc thi đấu",
                "Điểm số trong QuizMon được tính dựa trên số câu trả lời đúng và độ khó của câu hỏi.\n\n" +
                        "Bạn có thể tham gia các bảng xếp hạng để so sánh thành tích với người chơi khác.\n\n" +
                        "Càng trả lời đúng nhiều câu hỏi khó, bạn càng nhận được nhiều điểm thưởng và cơ hội thăng hạng."
            ),
            FaqItem(
                "Quảng cáo",
                "QuizMon có thể hiển thị quảng cáo để duy trì hoạt động và phát triển nội dung miễn phí.\n\n" +
                        "Một số quảng cáo có thể mang lại phần thưởng như xu hoặc lượt chơi.\n\n" +
                        "Bạn có thể tắt quảng cáo bằng cách nâng cấp tài khoản nếu tính năng này có sẵn."
            ),
            FaqItem(
                "Quyền riêng tư và bảo mật",
                "Chúng tôi cam kết bảo vệ thông tin cá nhân của bạn.\n\n" +
                        "Dữ liệu người dùng được lưu trữ an toàn và không chia sẻ cho bên thứ ba nếu không có sự đồng ý.\n\n" +
                        "Bạn có thể xem chi tiết trong phần chính sách bảo mật của ứng dụng."
            ),
            FaqItem(
                "Mini-game",
                "Mini-game là các trò chơi phụ giúp bạn kiếm thêm điểm hoặc phần thưởng.\n\n" +
                        "Các mini-game thường có luật chơi đơn giản nhưng yêu cầu phản xạ nhanh và chính xác.\n\n" +
                        "Đây là cách tuyệt vời để luyện tập và thư giãn trong quá trình chơi."
            ),
            FaqItem(
                "Nhiệm vụ",
                "Nhiệm vụ hàng ngày giúp bạn nhận thêm phần thưởng khi hoàn thành.\n\n" +
                        "Bạn có thể kiểm tra danh sách nhiệm vụ trong mục tương ứng.\n\n" +
                        "Hoàn thành nhiệm vụ liên tục sẽ mang lại phần thưởng lớn hơn."
            ),
            FaqItem(
                "Các vấn đề khác",
                "Nếu bạn không tìm thấy câu trả lời phù hợp, vui lòng sử dụng nút 'Báo cáo vấn đề'.\n\n" +
                        "Đội ngũ hỗ trợ của QuizMon sẽ phản hồi sớm nhất để giúp bạn giải quyết vấn đề."
            )
        )

        val adapter = FaqAdapter(faqList) { item ->
            val intent = Intent(this, FaqDetailActivity::class.java)
            intent.putExtra("title", item.title)
            intent.putExtra("content", item.content)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnReportFromFaq).setOnClickListener {
            showReportDialog()
        }
    }

    private fun showReportDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_report, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.findViewById<View>(R.id.btnContent).setOnClickListener {
            openReportForm("Nội dung")
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnTech).setOnClickListener {
            openReportForm("Kỹ thuật")
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnAds).setOnClickListener {
            openReportForm("Quảng cáo")
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnPrivacy).setOnClickListener {
            openReportForm("Quyền riêng tư")
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnOther).setOnClickListener {
            openReportForm("Khác")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openReportForm(type: String) {
        val intent = Intent(this, ReportActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}