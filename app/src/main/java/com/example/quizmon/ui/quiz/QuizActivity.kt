package com.example.quizmon.ui.quiz

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.R
import com.example.quizmon.data.model.Question
import com.example.quizmon.data.repository.QuizRepository
import com.example.quizmon.data.repository.StatisticsRepository
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.google.android.material.button.MaterialButton

/**
 * Hoạt động chính của phần trả lời câu hỏi Quiz.
 * Quản lý việc hiển thị câu hỏi, xử lý đáp án và sử dụng các phụ trợ.
 * Cơ chế: Khi nhấn vào phụ trợ sẽ hiện Dialog xác nhận tùy chỉnh kèm mô tả.
 */
class QuizActivity : AppCompatActivity() {

    companion object {
        const val RESULT_ANSWER_WRONG = 2 // Mã kết quả trả về màn hình bản đồ khi sai
        const val EXTRA_IS_DOUBLE_SCORE = "IS_DOUBLE_SCORE" // Key truyền trạng thái x2 về SubMap
    }

    // --- Khai báo thành phần giao diện ---
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var btnA: MaterialButton
    private lateinit var btnB: MaterialButton
    private lateinit var btnC: MaterialButton
    private lateinit var btnD: MaterialButton
    private lateinit var btnConfirm: MaterialButton
    private lateinit var cardExplanation: CardView
    private lateinit var tvExplanation: TextView
    private lateinit var btnBack: View

    // --- Các nút phụ trợ và hiển thị số lượng ---
    private lateinit var btnFiftyFifty: LinearLayout
    private lateinit var btnDoubleChance: LinearLayout
    private lateinit var btnRevealAnswer: LinearLayout
    private lateinit var btnDoubleScore: LinearLayout
    private lateinit var tvCountFiftyFifty: TextView
    private lateinit var tvCountDoubleChance: TextView
    private lateinit var tvCountRevealAnswer: TextView
    private lateinit var tvCountDoubleScore: TextView

    // Danh sách đáp án để quản lý chung
    private val answerButtons: List<MaterialButton> by lazy {
        listOf(btnA, btnB, btnC, btnD)
    }

    private lateinit var quizRepository: QuizRepository
    private lateinit var statisticsRepository: StatisticsRepository
    private lateinit var preferenceManager: PreferenceManager

    // --- Biến trạng thái câu hỏi ---
    private var currentQuestion: Question? = null
    private var selectedIndex = -1 // Vị trí đáp án đang chọn (-1 là chưa chọn)
    private var isAnswered = false // false: đang chọn, true: đã bấm Xác nhận chốt đáp án
    
    // --- Biến trạng thái phụ trợ (đang áp dụng cho câu hiện tại) ---
    private var isDoubleChanceActive = false
    private var isDoubleScoreActive = false
    private var isRevealAnswerActive = false
    
    private var currentCategory: String = "" // Tên chủ đề

    private var questionList: List<Question> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        // Căn chỉnh lề an toàn cho giao diện tràn viền
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        quizRepository = QuizRepository(this)
        statisticsRepository = StatisticsRepository(this)
        preferenceManager = PreferenceManager(this)

        currentCategory = intent.getStringExtra("CATEGORY") ?: "KienThucChung"
        val levelId = intent.getIntExtra("LEVEL_ID", 1)

        loadQuestion(currentCategory, levelId)
        setupConfirmButton()
        setupPowerUps()
        updatePowerUpCounts()

        btnBack.setOnClickListener { 
            SoundManager.playClick()
            showExitConfirmation() 
        }
        onBackPressedDispatcher.addCallback(this) { 
            SoundManager.playClick()
            showExitConfirmation() 
        }
    }

    private fun initViews() {
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber)
        tvQuestion = findViewById(R.id.tvQuestion)
        progressQuiz = findViewById(R.id.progressQuiz)
        btnA = findViewById(R.id.btnA)
        btnB = findViewById(R.id.btnB)
        btnC = findViewById(R.id.btnC)
        btnD = findViewById(R.id.btnD)
        btnConfirm = findViewById(R.id.btnConfirm)
        cardExplanation = findViewById(R.id.cardExplanation)
        tvExplanation = findViewById(R.id.tvExplanation)
        btnBack = findViewById(R.id.btnBack)

        btnFiftyFifty = findViewById(R.id.btnFiftyFifty)
        btnDoubleChance = findViewById(R.id.btnDoubleChance)
        btnRevealAnswer = findViewById(R.id.btnRevealAnswer)
        btnDoubleScore = findViewById(R.id.btnDoubleScore)
        
        tvCountFiftyFifty = findViewById(R.id.tvCountFiftyFifty)
        tvCountDoubleChance = findViewById(R.id.tvCountDoubleChance)
        tvCountRevealAnswer = findViewById(R.id.tvCountRevealAnswer)
        tvCountDoubleScore = findViewById(R.id.tvCountDoubleScore)

        resetPowerUpStyles()
    }

    private fun updatePowerUpCounts() {
        tvCountFiftyFifty.text = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_5050).toString()
        tvCountDoubleChance.text = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_DOUBLE_CHANCE).toString()
        tvCountRevealAnswer.text = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_CORRECT_ANSWER).toString()
        tvCountDoubleScore.text = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_DOUBLE_POINTS).toString()
    }

    private fun loadQuestion(category: String, levelId: Int) {
        val fileName = categoryToFileName(category)
        questionList = quizRepository.getQuestionsByTopic(fileName, limit = 10)
        currentQuestion = questionList.find { it.id.toString() == levelId.toString() }
            ?: questionList.randomOrNull()

        currentQuestion?.let { displayQuestion(it) } ?: run {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun displayQuestion(q: Question) {
        tvQuestion.text = q.question
        tvQuestionNumber.text = getCategoryDisplayName(currentCategory)
        
        progressQuiz.progress = 50
        cardExplanation.visibility = View.GONE
        
        isAnswered = false
        btnConfirm.isEnabled = false
        btnConfirm.text = "Xác nhận"
        
        // Reset trạng thái phụ trợ khi sang câu mới
        isDoubleChanceActive = false
        isDoubleScoreActive = false
        isRevealAnswerActive = false
        
        resetButtonStyles()
        resetPowerUpStyles()

        q.options.forEachIndexed { index, option ->
            if (index < answerButtons.size) {
                answerButtons[index].apply {
                    text = "${"ABCD"[index]}. $option"
                    visibility = View.VISIBLE
                    isEnabled = true
                    alpha = 1f
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setOnClickListener { onAnswerSelected(index) }
                }
            }
        }
        for (i in q.options.size until answerButtons.size) {
            answerButtons[i].visibility = View.GONE
        }

        btnA.post {
            var maxHeight = 0
            answerButtons.forEach { btn ->
                if (btn.visibility == View.VISIBLE) {
                    val h = btn.measuredHeight
                    if (h > maxHeight) maxHeight = h
                }
            }
            if (maxHeight > 0) {
                answerButtons.forEach { btn ->
                    if (btn.visibility == View.VISIBLE) {
                        val params = btn.layoutParams
                        params.height = maxHeight
                        btn.layoutParams = params
                    }
                }
            }
        }
    }

    private fun resetPowerUpStyles() {
        listOf(btnFiftyFifty, btnDoubleChance, btnRevealAnswer, btnDoubleScore).forEach {
            setPowerUpBackground(it, false)
            it.alpha = 1.0f
            it.isClickable = true
            it.isEnabled = true
        }
    }

    /**
     * Thiết lập background bo góc và viền cho ô phụ trợ
     */
    private fun setPowerUpBackground(view: View, isActive: Boolean) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 14f.dpToPx() // Bo góc
            if (isActive) {
                setColor("#FFE0B2".toColorInt()) 
                setStroke(3f.dpToPx().toInt(), "#FB8C00".toColorInt()) // Viền cam đậm 3dp
            } else {
                setColor("#F5F5F5".toColorInt()) // Nền xám nhạt khi tắt
                setStroke(2f.dpToPx().toInt(), "#BCAAA4".toColorInt()) // Viền xám nâu 2dp
            }
        }
        view.background = drawable
        val p = 8f.dpToPx().toInt()
        view.setPadding(p, p, p, p)
    }

    private fun Float.dpToPx(): Float = this * resources.displayMetrics.density

    private fun onAnswerSelected(index: Int) {
        if (isAnswered) return 
        SoundManager.playClick()
        selectedIndex = index
        resetButtonStyles()
        answerButtons[index].backgroundTintList =
            ContextCompat.getColorStateList(this, android.R.color.holo_orange_light)
        
        // Nếu đang bật "Gợi ý", luôn hiển thị đáp án đúng màu xanh
        if (isRevealAnswerActive && currentQuestion != null) {
            val correctIdx = currentQuestion!!.correctIndex
            answerButtons[correctIdx].backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_light)
            answerButtons[correctIdx].setTextColor(Color.WHITE)
        }
        
        animatePressButton(answerButtons[index])
        btnConfirm.isEnabled = true
    }

    private fun setupConfirmButton() {
        btnConfirm.setOnClickListener {
            val q = currentQuestion ?: return@setOnClickListener

            if (!isAnswered) {
                if (selectedIndex == -1) return@setOnClickListener
                SoundManager.playClick()
                val isCorrect = selectedIndex == q.correctIndex

                // Xử lý Phụ trợ "Cơ hội 2" khi trả lời sai lần đầu
                if (!isCorrect && isDoubleChanceActive) {
                    SoundManager.playWrong()
                    isDoubleChanceActive = false 
                    val indexToHide = selectedIndex 
                    Toast.makeText(this, "Đáp án sai! Bạn còn 1 cơ hội.", Toast.LENGTH_SHORT).show()
                    
                    answerButtons[indexToHide].animate().alpha(0f).setDuration(300).withEndAction {
                        answerButtons[indexToHide].visibility = View.INVISIBLE
                        answerButtons[indexToHide].isEnabled = false
                    }.start()
                    
                    selectedIndex = -1
                    btnConfirm.isEnabled = false
                    return@setOnClickListener
                }

                // Chốt câu trả lời
                isAnswered = true
                answerButtons.forEach { it.isEnabled = false }
                
                // Vô hiệu hóa tất cả ô phụ trợ khi đã trả lời xong
                listOf(btnFiftyFifty, btnDoubleChance, btnRevealAnswer, btnDoubleScore).forEach {
                    disablePowerUp(it)
                }

                if (isCorrect) {
                    SoundManager.playCorrect()
                    animateCorrect(selectedIndex)
                    preferenceManager.handleCorrectAnswer()
                } else {
                    SoundManager.playWrong()
                    animateWrong(selectedIndex, q.correctIndex)
                    preferenceManager.handleWrongAnswer()
                }

                showExplanation(q.explanation)

                statisticsRepository.saveQuizResult(
                    correct = if (isCorrect) 1 else 0,
                    wrong = if (isCorrect) 0 else 1
                )

                btnConfirm.text = "Tiếp tục"
                btnConfirm.isEnabled = true
            } else {
                SoundManager.playClick()
                // Thoát và truyền trạng thái x2 điểm về SubMapActivity
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_IS_DOUBLE_SCORE, isDoubleScoreActive)
                val isCorrect = selectedIndex == q.correctIndex
                setResult(if (isCorrect) RESULT_OK else RESULT_ANSWER_WRONG, resultIntent)
                finish()
            }
        }
    }

    private fun setupPowerUps() {
        // --- Nhấn để hiện Dialog xác nhận sử dụng ---
        btnFiftyFifty.setOnClickListener {
            SoundManager.playClick()
            showPowerUpConfirmation("50/50", "Loại bỏ 2 phương án trả lời sai.") {
                useFiftyFifty()
            }
        }
        btnDoubleChance.setOnClickListener {
            SoundManager.playClick()
            showPowerUpConfirmation("Cơ hội 2", "Cho phép bạn chọn lại một lần nếu trả lời sai.") {
                useDoubleChance()
            }
        }
        btnRevealAnswer.setOnClickListener {
            SoundManager.playClick()
            showPowerUpConfirmation("Gợi ý", "Hiển thị trực tiếp đáp án chính xác cho câu hỏi này.") {
                useRevealAnswer()
            }
        }
        btnDoubleScore.setOnClickListener {
            SoundManager.playClick()
            showPowerUpConfirmation("x2 Điểm", "Nhận gấp đôi số điểm nếu bạn trả lời đúng câu hỏi này.") {
                useDoubleScore()
            }
        }
    }

    /**
     * Hiển thị Dialog xác nhận tùy chỉnh trước khi dùng phụ trợ
     */
    private fun showPowerUpConfirmation(name: String, desc: String, onConfirm: () -> Unit) {
        if (isAnswered) return

        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_style, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnPositive = dialogView.findViewById<MaterialButton>(R.id.btnPositive)
        val btnNegative = dialogView.findViewById<MaterialButton>(R.id.btnNegative)

        tvTitle.text = "Sử dụng $name?"
        tvMessage.text = "$desc\n\nBạn có chắc chắn muốn dùng phụ trợ này không?"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Làm cho nền của AlertDialog trong suốt để thấy bo góc và background custom
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnPositive.setOnClickListener {
            SoundManager.playClick()
            onConfirm()
            dialog.dismiss()
        }
        btnNegative.setOnClickListener {
            SoundManager.playClick()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun useFiftyFifty() {
        val qty = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_5050)
        if (qty <= 0) {
            Toast.makeText(this, "Bạn đã hết phụ trợ 50/50", Toast.LENGTH_SHORT).show()
            return
        }
        SoundManager.playBonus()
        val q = currentQuestion ?: return
        
        preferenceManager.addSupport(PreferenceManager.SUPPORT_5050, -1)
        updatePowerUpCounts()
        
        val wrongIndexes = answerButtons.indices
            .filter { it != q.correctIndex && answerButtons[it].visibility == View.VISIBLE }
            .shuffled().take(2)
        
        wrongIndexes.forEach { idx ->
            answerButtons[idx].animate().alpha(0f).setDuration(300).withEndAction {
                answerButtons[idx].isEnabled = false
                answerButtons[idx].visibility = View.INVISIBLE
            }.start()
        }
        
        if (selectedIndex in wrongIndexes) {
            selectedIndex = -1
            btnConfirm.isEnabled = false
            resetButtonStyles()
        }
        
        setPowerUpBackground(btnFiftyFifty, true)
        disablePowerUp(btnFiftyFifty)
    }

    private fun useDoubleChance() {
        val qty = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_DOUBLE_CHANCE)
        if (qty <= 0) {
            Toast.makeText(this, "Bạn không còn phụ trợ Cơ hội 2", Toast.LENGTH_SHORT).show()
            return
        }
        SoundManager.playBonus()
        
        preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_CHANCE, -1)
        updatePowerUpCounts()
        
        isDoubleChanceActive = true
        setPowerUpBackground(btnDoubleChance, true)
        disablePowerUp(btnDoubleChance)
    }

    private fun useRevealAnswer() {
        val qty = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_CORRECT_ANSWER)
        if (qty <= 0) {
            Toast.makeText(this, "Bạn không còn phụ trợ Gợi ý", Toast.LENGTH_SHORT).show()
            return
        }
        SoundManager.playBonus()
        val q = currentQuestion ?: return
        
        preferenceManager.addSupport(PreferenceManager.SUPPORT_CORRECT_ANSWER, -1)
        updatePowerUpCounts()
        
        isRevealAnswerActive = true
        answerButtons[q.correctIndex].apply {
            visibility = View.VISIBLE
            isEnabled = true
            alpha = 1f
            backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, android.R.color.holo_green_light)
            setTextColor(Color.WHITE)
        }
        
        setPowerUpBackground(btnRevealAnswer, true)
        disablePowerUp(btnRevealAnswer)
        onAnswerSelected(q.correctIndex)
    }

    private fun useDoubleScore() {
        val qty = preferenceManager.getSupportQuantity(PreferenceManager.SUPPORT_DOUBLE_POINTS)
        if (qty <= 0) {
            Toast.makeText(this, "Bạn không còn phụ trợ x2 Điểm", Toast.LENGTH_SHORT).show()
            return
        }
        SoundManager.playBonus()
        
        preferenceManager.addSupport(PreferenceManager.SUPPORT_DOUBLE_POINTS, -1)
        updatePowerUpCounts()
        
        isDoubleScoreActive = true
        setPowerUpBackground(btnDoubleScore, true)
        disablePowerUp(btnDoubleScore)
    }

    private fun disablePowerUp(view: LinearLayout) {
        view.animate().alpha(0.35f).setDuration(300).start()
        view.isClickable = false
        view.isEnabled = false
    }

    private fun animateCorrect(index: Int) {
        if (index !in answerButtons.indices) return
        val btn = answerButtons[index]
        btn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_green_light)
        btn.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.08f, 1f),
                ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.08f, 1f)
            )
            duration = 350
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun animateWrong(wrongIndex: Int, correctIndex: Int) {
        if (wrongIndex in answerButtons.indices) {
            answerButtons[wrongIndex].apply {
                backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, android.R.color.holo_red_light)
                setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.white))
            }
            ObjectAnimator.ofFloat(answerButtons[wrongIndex], "translationX", 0f, -16f, 16f, -12f, 12f, -8f, 8f, 0f).apply {
                duration = 450
                start()
            }
        }
        if (correctIndex in answerButtons.indices) {
            answerButtons[correctIndex].apply {
                backgroundTintList = ContextCompat.getColorStateList(this@QuizActivity, android.R.color.holo_green_light)
                setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.white))
            }
        }
    }

    private fun showExplanation(explanation: String?) {
        if (explanation.isNullOrBlank()) return
        tvExplanation.text = explanation
        cardExplanation.visibility = View.VISIBLE
        cardExplanation.translationY = 40f
        cardExplanation.alpha = 0f
        cardExplanation.animate().translationY(0f).alpha(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
    }

    private fun showExitConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Thoát trò chơi?")
            .setMessage("Tiến trình câu hỏi này sẽ không được lưu. Bạn có chắc muốn thoát không?")
            .setPositiveButton("Thoát") { _, _ ->
                setResult(RESULT_CANCELED)
                finish()
            }
            .setNegativeButton("Ở lại", null)
            .show()
    }

    private fun resetButtonStyles() {
        answerButtons.forEach { btn ->
            btn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.white)
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }
    }

    private fun animatePressButton(btn: MaterialButton) {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(btn, "scaleX", 1f, 0.95f, 1f),
                ObjectAnimator.ofFloat(btn, "scaleY", 1f, 0.95f, 1f)
            )
            duration = 150
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        // Phát nhạc nền Quiz
        SoundManager.playMusic(this, R.raw.quiz)
    }

    override fun onPause() {
        super.onPause()
        SoundManager.pauseMusic()
    }

    private fun getCategoryDisplayName(category: String): String = when (category) {
        "KienThucChung"  -> "Kiến thức chung"
        "CNXHKH"         -> "CNXHKH"
        "DiaLy"          -> "Địa lý"
        "HoaHoc"         -> "Hóa học"
        "KinhTeChinhTri" -> "Kinh tế chính trị"
        "LichSu"         -> "Lịch sử"
        "TinHoc"         -> "Công nghệ"
        "TuTuongHCM"     -> "Tư tưởng HCM"
        "VanHoc"         -> "Văn học"
        "VatLy"          -> "Vật lý"
        "AmNhac"         -> "Âm nhạc"
        "ChoiChu"        -> "Chơi chữ"
        "TiengAnh"       -> "Tiếng Anh"
        "DoVui"          -> "Đố vui"
        else             -> "Câu hỏi"
    }

    private fun categoryToFileName(category: String): String = when (category) {
        "KienThucChung"  -> "KTC_questions.json"
        "CNXHKH"         -> "CNXHKH_questions.json"
        "DiaLy"          -> "D_questions.json"
        "HoaHoc"         -> "H_questions.json"
        "KinhTeChinhTri" -> "KTCT_questions.json"
        "LichSu"         -> "S_questions.json"
        "TinHoc"         -> "TH_questions.json"
        "TuTuongHCM"     -> "TTHCM_questions.json"
        "VanHoc"         -> "V_questions.json"
        "VatLy"          -> "VL_questions.json"
        "AmNhac"         -> "AN_questions.json"
        "ChoiChu"        -> "CC_questions.json"
        "TiengAnh"       -> "E_questions.json"
        "DoVui"          -> "DoVui_questions.json"
        else             -> "questions.json"
    }
}
