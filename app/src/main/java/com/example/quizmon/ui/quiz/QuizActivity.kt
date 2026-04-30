package com.example.quizmon.ui.quiz
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.R
import com.example.quizmon.data.repository.QuizRepository
import com.example.quizmon.data.repository.StatisticsRepository
import com.example.quizmon.data.model.Question
import com.example.quizmon.utils.PreferenceManager
import com.google.android.material.button.MaterialButton

class QuizActivity : AppCompatActivity() {

    companion object {
        const val RESULT_ANSWER_WRONG = 2
    }

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

     //khai báo hiến của 4 ô phụ trợ
    private lateinit var btnFiftyFifty: LinearLayout
    private lateinit var btnDoubleQuestion: LinearLayout
    private lateinit var btnRevealAnswer: LinearLayout
    private lateinit var btnDoubleScore: LinearLayout

    private val answerButtons: List<MaterialButton> by lazy {
        listOf(btnA, btnB, btnC, btnD)
    }

//--------------Repository-----------------
    private lateinit var quizRepository: QuizRepository
    private lateinit var statisticsRepository: StatisticsRepository
    private lateinit var preferenceManager: PreferenceManager

// Trạng thái thiết lập dữ liệu khi mới bắt đầu
    private var currentQuestion: Question? = null
    private var selectedIndex = -1

    private var questionList: List<Question> = emptyList()

    private var isDoubleScore = false   // phụ trợ x2 điểm đang bật

 // Mỗi phụ trợ dùng 1 lần
    private var usedFiftyFifty = false
    private var usedDoubleQuestion = false
    private var usedRevealAnswer = false
    private var usedDoubleScore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContentView(R.layout.activity_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        quizRepository = QuizRepository(this)
        statisticsRepository = StatisticsRepository(this)
        preferenceManager = PreferenceManager(this)

        val category = intent.getStringExtra("CATEGORY") ?: "CNXHKH"
        val levelId = intent.getIntExtra("LEVEL_ID", 1)

        loadQuestion(category, levelId)
        setupConfirmButton()
        setupPowerUps()
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
        btnFiftyFifty     = findViewById(R.id.btnFiftyFifty)
        btnDoubleQuestion = findViewById(R.id.btnDoubleQuestion)
        btnRevealAnswer   = findViewById(R.id.btnRevealAnswer)
        btnDoubleScore    = findViewById(R.id.btnDoubleScore)
        }

    private fun loadQuestion(category: String, levelId: Int) {
        val fileName = categoryToFileName(category)
        val questions = quizRepository.getQuestionsByTopic(fileName, limit = 10)
        currentQuestion = questions.find { it.id.toString() == levelId.toString() }
            ?: questions.randomOrNull()

        currentQuestion?.let { displayQuestion(it) } ?: run {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun displayQuestion(q: Question) {
        tvQuestion.text = q.question
        tvQuestionNumber.text = "Câu hỏi"
        progressQuiz.progress = 50
        cardExplanation.visibility = View.GONE
        btnConfirm.isEnabled = false
        resetButtonStyles()

        q.options.forEachIndexed { index, option ->
            if (index < answerButtons.size) {
                answerButtons[index].text = "${"ABCD"[index]}. $option"
                answerButtons[index].visibility = View.VISIBLE
                answerButtons[index].isEnabled = true
                answerButtons[index].setOnClickListener { onAnswerSelected(index) }
            }
        }
        for (i in q.options.size until answerButtons.size) {
            answerButtons[i].visibility = View.GONE
        }
    }

    private fun onAnswerSelected(index: Int) {
        selectedIndex = index
        resetButtonStyles()
        answerButtons[index].backgroundTintList =
            ContextCompat.getColorStateList(this, android.R.color.holo_orange_light)
        animatePressButton(answerButtons[index])
        btnConfirm.isEnabled = true
    }

    private fun setupConfirmButton() {
        btnConfirm.setOnClickListener {
            val q = currentQuestion ?: return@setOnClickListener
            val isCorrect = selectedIndex == q.correctIndex
            answerButtons.forEach { it.isEnabled = false }
            btnConfirm.isEnabled = false

            if (isCorrect) {
                animateCorrect(selectedIndex)
                preferenceManager.handleCorrectAnswer()
            } else {
                animateWrong(selectedIndex, q.correctIndex)
                preferenceManager.handleWrongAnswer()
            }

            showExplanation(q.explanation)

            Handler(Looper.getMainLooper()).postDelayed({
                statisticsRepository.saveQuizResult(
                    correct = if (isCorrect) 1 else 0,
                    wrong = if (isCorrect) 0 else 1
                )
                setResult(if (isCorrect) RESULT_OK else RESULT_ANSWER_WRONG)
                finish()
            }, 2000)
        }
    }

    private fun setupPowerUps() {
        btnFiftyFifty.setOnClickListener {
            if (usedFiftyFifty) return@setOnClickListener
            val q = currentQuestion ?: return@setOnClickListener

            val wrongIndexes = answerButtons.indices
                .filter { it != q.correctIndex && answerButtons[it].visibility == View.VISIBLE }
                .shuffled()
                .take(2)

            wrongIndexes.forEach { idx ->
                answerButtons[idx].animate().alpha(0f).setDuration(300).withEndAction {
                    answerButtons[idx].isEnabled = false
                    answerButtons[idx].visibility = View.INVISIBLE
                }.start()
            }

            usedFiftyFifty = true
            disablePowerUp(btnFiftyFifty)
        }

        btnDoubleQuestion.setOnClickListener {
            if (usedDoubleQuestion) return@setOnClickListener

            val newQuestion = questionList
                .filter { it.id != currentQuestion?.id }
                .randomOrNull()

            newQuestion?.let {
                currentQuestion = it
                displayQuestion(it)
            }

            usedDoubleQuestion = true
            disablePowerUp(btnDoubleQuestion)
        }

        btnRevealAnswer.setOnClickListener {
            if (usedRevealAnswer) return@setOnClickListener
            val q = currentQuestion ?: return@setOnClickListener

            answerButtons[q.correctIndex].apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(this@QuizActivity, android.R.color.holo_green_light)
                setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.white))
            }
            onAnswerSelected(q.correctIndex)

            usedRevealAnswer = true
            disablePowerUp(btnRevealAnswer)
        }

        btnDoubleScore.setOnClickListener {
            if (usedDoubleScore) return@setOnClickListener

            isDoubleScore = true
            ObjectAnimator.ofFloat(btnDoubleScore, "alpha", 1f, 0.4f, 1f, 0.4f, 1f).apply {
                duration = 600
                start()
            }

            usedDoubleScore = true
            disablePowerUp(btnDoubleScore)
        }
    }

    private fun disablePowerUp(view: LinearLayout) {
        view.animate().alpha(0.35f).setDuration(300).start()
        view.isClickable = false
    }

    private fun animateCorrect(index: Int) {
        val btn = answerButtons[index]
        btn.backgroundTintList =
            ContextCompat.getColorStateList(this, android.R.color.holo_green_light)
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
        answerButtons[wrongIndex].apply {
            backgroundTintList =
                ContextCompat.getColorStateList(this@QuizActivity, android.R.color.holo_red_light)
            setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.white))
        }
        if (correctIndex in answerButtons.indices) {
            answerButtons[correctIndex].apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(this@QuizActivity, android.R.color.holo_green_light)
                setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.white))
            }
        }
        ObjectAnimator.ofFloat(
            answerButtons[wrongIndex], "translationX",
            0f, -16f, 16f, -12f, 12f, -8f, 8f, 0f
        ).apply { duration = 450; start() }
    }

    private fun showExplanation(explanation: String?) {
        if (explanation.isNullOrBlank()) return
        tvExplanation.text = explanation
        cardExplanation.visibility = View.VISIBLE
        cardExplanation.translationY = 40f
        cardExplanation.alpha = 0f
        cardExplanation.animate()
            .translationY(0f).alpha(1f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun resetButtonStyles() {
        answerButtons.forEach { btn ->
            btn.backgroundTintList =
                ContextCompat.getColorStateList(this, android.R.color.white)
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
