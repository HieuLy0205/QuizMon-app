package com.example.quizmon.ui.history
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizmon.MainActivity
import com.example.quizmon.R
import com.example.quizmon.data.source.local.HistoryRecord
import com.example.quizmon.ui.shop.activity_shop
import com.example.quizmon.ui.profile.ProfileActivity
import com.example.quizmon.ui.settings.SettingsActivity
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.SoundManager
import com.example.quizmon.utils.TaskHeadManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var adapter: HistoryAdapter

    // Views từ activity_history.xml (giữ nguyên id đã có)
    private lateinit var rvHistory    : RecyclerView
    private lateinit var tvEmpty      : TextView
    private lateinit var tvStatCorrect: TextView
    private lateinit var tvStatWrong  : TextView
    private lateinit var tvStatTotal  : TextView
    private lateinit var btnFilter    : View
    private lateinit var tvFilterBadge: TextView

    // Trạng thái tạm trong bottom sheet (chưa áp dụng)
    private var tempCategory   = "all"
    private var tempAnswerType = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        preferenceManager = PreferenceManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        setupRecyclerView()
        observeViewModel()
        setupTaskbar()       // giữ nguyên logic gốc
    }

    // ── Bind views ───────────────────────────────────────────────────────────
    private fun bindViews() {
        rvHistory     = findViewById(R.id.rv_history)
        tvEmpty       = findViewById(R.id.tv_empty)
        tvStatCorrect = findViewById(R.id.tv_stat_correct)
        tvStatWrong   = findViewById(R.id.tv_stat_wrong)
        tvStatTotal   = findViewById(R.id.tv_stat_total)
        btnFilter     = findViewById(R.id.btn_filter)
        tvFilterBadge = findViewById(R.id.tv_filter_badge)

        btnFilter.setOnClickListener {
            SoundManager.playClick()
            showFilterSheet()
        }
    }

    // ── RecyclerView ─────────────────────────────────────────────────────────
    private fun setupRecyclerView() {
        adapter = HistoryAdapter { record ->
            SoundManager.playClick()
            showDetailSheet(record)
        }
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    // ── Observe ViewModel ────────────────────────────────────────────────────
    private fun observeViewModel() {
        viewModel.historyList.observe(this) { records ->
            adapter.submitList(HistoryAdapter.buildItems(records))

            val correct = records.count { it.isCorrect }
            tvStatCorrect.text = correct.toString()
            tvStatWrong.text   = (records.size - correct).toString()
            tvStatTotal.text   = records.size.toString()

            tvEmpty.isVisible   = records.isEmpty()
            rvHistory.isVisible = records.isNotEmpty()
        }
    }

    // ── Bottom Sheet: BỘ LỌC ────────────────────────────────────────────────
    private fun showFilterSheet() {
        tempCategory   = viewModel.selectedCategory.value   ?: "all"
        tempAnswerType = viewModel.selectedAnswerType.value ?: "all"

        val sheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view  = layoutInflater.inflate(R.layout.sheet_history_filter, null)
        sheet.setContentView(view)

        val chipGroupCat : ChipGroup = view.findViewById(R.id.chip_group_category)
        val chipGroupAns : ChipGroup = view.findViewById(R.id.chip_group_answer)
        val btnApply     : Button    = view.findViewById(R.id.btn_apply)
        val btnReset     : Button    = view.findViewById(R.id.btn_reset)

        // Chips thể loại — động từ DB (chỉ hiện thể loại đã chơi)
        fun buildCatChips(cats: List<String>) {
            chipGroupCat.removeAllViews()
            chipGroupCat.addView(makeChip("Tất cả", "all", tempCategory == "all"))
            cats.forEach { cat ->
                chipGroupCat.addView(makeChip(cat, cat, tempCategory == cat))
            }
        }
        viewModel.categories.observe(this) { buildCatChips(it) }

        // Chips loại đáp án — cố định 3 chip
        chipGroupAns.removeAllViews()
        listOf("Tất cả" to "all", "Đúng" to "correct", "Sai" to "wrong").forEach { (label, value) ->
            chipGroupAns.addView(makeChip(label, value, tempAnswerType == value))
        }

        chipGroupCat.setOnCheckedStateChangeListener { group, ids ->
            if (ids.isNotEmpty())
                tempCategory = group.findViewById<Chip>(ids.first())?.tag as? String ?: "all"
        }
        chipGroupAns.setOnCheckedStateChangeListener { group, ids ->
            if (ids.isNotEmpty())
                tempAnswerType = group.findViewById<Chip>(ids.first())?.tag as? String ?: "all"
        }

        btnApply.setOnClickListener {
            SoundManager.playClick()
            viewModel.applyFilter(tempCategory, tempAnswerType)
            tvFilterBadge.isVisible = viewModel.isFiltered
            sheet.dismiss()
        }

        btnReset.setOnClickListener {
            SoundManager.playClick()
            tempCategory   = "all"
            tempAnswerType = "all"
            (chipGroupCat.getChildAt(0) as? Chip)?.isChecked = true
            (chipGroupAns.getChildAt(0) as? Chip)?.isChecked = true
        }

        sheet.show()
    }

    private fun makeChip(label: String, tag: String, checked: Boolean): Chip =
        Chip(this).apply {
            text        = label
            this.tag    = tag
            isCheckable = true
            isChecked   = checked
            setChipBackgroundColorResource(R.color.chip_bg_selector)
            setTextColor(resources.getColorStateList(R.color.chip_text_selector, theme))
        }

    // ── Bottom Sheet: CHI TIẾT CÂU HỎI ─────────────────────────────────────
    private fun showDetailSheet(record: HistoryRecord) {
        val sheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view  = layoutInflater.inflate(R.layout.sheet_history_detail, null)
        sheet.setContentView(view)

        val tvCategory : TextView     = view.findViewById(R.id.tv_detail_category)
        val tvResult   : TextView     = view.findViewById(R.id.tv_detail_result)
        val tvQuestion : TextView     = view.findViewById(R.id.tv_detail_question)
        val llOptions  : LinearLayout = view.findViewById(R.id.ll_options)
        val tvExplain  : TextView     = view.findViewById(R.id.tv_explain)

        tvCategory.text = record.category
        tvQuestion.text = record.questionText
        tvExplain.text  = record.explanation

        if (record.isCorrect) {
            tvResult.text = "✓ Trả lời đúng"
            tvResult.setBackgroundResource(R.drawable.bg_badge_correct)
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
        } else {
            tvResult.text = "✗ Trả lời sai"
            tvResult.setBackgroundResource(R.drawable.bg_badge_wrong)
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.wrong_red))
        }

        // Render các hàng đáp án
        val options = record.getOptions()
        val labels  = listOf("A", "B", "C", "D")
        llOptions.removeAllViews()

        options.forEachIndexed { index, optionText ->
            val row      = layoutInflater.inflate(R.layout.item_answer_option, llOptions, false)
            val tvLabel  : TextView   = row.findViewById(R.id.tv_option_label)
            val tvOption : TextView   = row.findViewById(R.id.tv_option_text)
            val ivIcon   : ImageView  = row.findViewById(R.id.iv_option_icon)

            tvLabel.text  = labels.getOrElse(index) { "" }
            tvOption.text = optionText

            when {
                // Đáp án đúng → nền xanh
                index == record.correctIndex -> {
                    row.setBackgroundResource(R.drawable.bg_option_correct)
                    tvLabel.setBackgroundResource(R.drawable.bg_option_correct)
                    tvLabel.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
                    tvOption.setTextColor(ContextCompat.getColor(this, R.color.correct_green_dark))
                    ivIcon.setImageResource(R.drawable.ic_check)
                    ivIcon.isVisible = true
                }
                // Đáp án sai người chơi chọn → nền đỏ
                index == record.chosenIndex && !record.isCorrect -> {
                    row.setBackgroundResource(R.drawable.bg_option_wrong)
                    tvLabel.setBackgroundResource(R.drawable.bg_option_wrong)
                    tvLabel.setTextColor(ContextCompat.getColor(this, R.color.wrong_red))
                    tvOption.setTextColor(ContextCompat.getColor(this, R.color.wrong_red_dark))
                    ivIcon.setImageResource(R.drawable.ic_close)
                    ivIcon.isVisible = true
                }
                // Đáp án bình thường
                else -> {
                    row.setBackgroundResource(R.drawable.bg_option_normal)
                    tvLabel.setBackgroundResource(R.drawable.bg_option_normal)
                    tvLabel.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
                    ivIcon.isVisible = false
                }
            }
            llOptions.addView(row)
        }

        sheet.show()
    }

    // ── Taskbar  ─────────────────────────────────
    private fun setupTaskbar() {
        findViewById<View>(R.id.indicator_history).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_nav_history)
            .setTextColor(ContextCompat.getColor(this, R.color.taskbar_active))

        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            SoundManager.playClick()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_shop).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, activity_shop::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_menu).setOnClickListener {
            SoundManager.playClick()
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    // ── onResume / onPause ──────────────────────
    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), preferenceManager)
        SoundManager.playMusic(this, R.raw.background)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
        SoundManager.pauseMusic()
    }
}