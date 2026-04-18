package com.example.quizmon.ui.level

enum class SubMapType {
    QUESTION,    // Ô câu hỏi (có logo chủ đề)
    TREASURE,    // Rương kho báu
    SPIN_WHEEL,  // Vòng quay bonus
    FLIP_CARD,   // Lật thẻ trùng
    DECORATION   // Ô trang trí hoặc ô trống
}

enum class CompletionStatus {
    NOT_STARTED,
    CORRECT,
    INCORRECT
}

data class SubMapItem(
    val id: String,
    val type: SubMapType,
    val category: String? = null,
    val logoResId: Int? = null,
    val status: CompletionStatus = CompletionStatus.NOT_STARTED,
    val x: Int,
    val y: Int
)