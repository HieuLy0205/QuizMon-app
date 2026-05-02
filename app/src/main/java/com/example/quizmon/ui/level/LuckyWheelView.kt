package com.example.quizmon.ui.level

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class LuckyWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val items = listOf(
        "Trừ 20\nĐiểm", "Cộng 40\nĐiểm", "Phụ trợ\n50/50 x1",
        "Nhân đôi\ncơ hội x1", "Đáp án\nđúng x1", "Nhân đôi\nđiểm x1",
        "Cộng 10\nXu", "Cộng 15\nEXP", "Chúc mừng\nngày mới"
    )

    private val colors = listOf(
        "#FF7043", "#66BB6A", "#42A5F5",
        "#FFEE58", "#EC407A", "#AB47BC",
        "#FFA726", "#26C6DA", "#9CCC65"
    )

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        // Hiệu ứng bóng đổ cho chữ chuyên nghiệp hơn
        setShadowLayer(3f, 0f, 0f, Color.BLACK)
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4E342E")
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width.coerceAtMost(height) / 2f) - 30f

        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        val sweepAngle = 360f / items.size

        for (i in items.indices) {
            arcPaint.color = Color.parseColor(colors[i % colors.size])
            // Vẽ các cung tròn. -90 là bắt đầu từ hướng 12 giờ.
            val startAngle = i * sweepAngle - 90f
            canvas.drawArc(rect, startAngle, sweepAngle, true, arcPaint)

            // Vẽ nội dung item
            drawTextOnArc(canvas, centerX, centerY, radius, startAngle, sweepAngle, items[i])
        }

        // Vẽ viền trang trí
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
        
        // Vẽ tâm điểm
        arcPaint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, 40f, arcPaint)
        canvas.drawCircle(centerX, centerY, 40f, borderPaint)
    }

    private fun drawTextOnArc(canvas: Canvas, cx: Float, cy: Float, radius: Float, startAngle: Float, sweep: Float, text: String) {
        val angle = Math.toRadians((startAngle + sweep / 2).toDouble())
        val textRadius = radius * 0.65f
        val x = (cx + textRadius * cos(angle)).toFloat()
        val y = (cy + textRadius * sin(angle)).toFloat()

        canvas.save()
        canvas.translate(x, y)
        // Xoay text theo hướng nan quạt
        canvas.rotate(startAngle + sweep / 2 + 90)

        val lines = text.split("\n")
        textPaint.textSize = width * 0.032f
        
        var yOffset = -(lines.size - 1) * textPaint.textSize / 2
        for (line in lines) {
            canvas.drawText(line, 0f, yOffset, textPaint)
            yOffset += textPaint.textSize + 5
        }
        canvas.restore()
    }

    fun getItemsCount(): Int = items.size
}
