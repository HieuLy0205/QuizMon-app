package com.example.quizmon.utils

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import com.example.quizmon.data.model.Statistics

object ChartHelper {

    fun createBarChart(stats: List<Statistics>, width: Int, height: Int): BitmapDrawable {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Vẽ nền trắng
        canvas.drawColor(Color.WHITE)

        if (stats.isEmpty()) {
            paint.color = Color.GRAY
            paint.textSize = 40f
            canvas.drawText("Chưa có dữ liệu", width/2f - 100f, height/2f, paint)
            return BitmapDrawable(bitmap)
        }

        val maxValue = stats.maxOfOrNull { it.correctAnswers + it.wrongAnswers } ?: 10
        val barWidth = (width - 100f) / stats.size
        val startX = 50f

        for (i in stats.indices) {
            val stat = stats[i]
            val totalQuestions = (stat.correctAnswers + stat.wrongAnswers).toFloat()
            val barHeight = if (maxValue > 0) (totalQuestions / maxValue) * (height - 150f) else 0f

            // Vẽ cột
            paint.color = Color.parseColor("#4CAF50")
            val left = startX + i * barWidth
            val right = left + barWidth - 10f
            val bottom = height - 50f
            val top = bottom - barHeight
            canvas.drawRect(left, top, right, bottom, paint)

            // Vẽ số đúng (xanh)
            if (stat.correctAnswers > 0) {
                paint.color = Color.parseColor("#2196F3")
                val correctHeight = (stat.correctAnswers.toFloat() / maxValue) * (height - 150f)
                val correctTop = bottom - correctHeight
                canvas.drawRect(left, correctTop, right, bottom, paint)
            }

            // Vẽ nhãn ngày
            paint.color = Color.BLACK
            paint.textSize = 30f
            val dateStr = "${stat.date.date}/${stat.date.month + 1}"
            canvas.drawText(dateStr, left + 10f, bottom + 30f, paint)
        }

        // Vẽ chú thích
        paint.textSize = 35f
        paint.color = Color.parseColor("#2196F3")
        canvas.drawText("■ Đúng", width - 150f, 50f, paint)
        paint.color = Color.parseColor("#4CAF50")
        canvas.drawText("■ Tổng", width - 150f, 100f, paint)

        return BitmapDrawable(bitmap)
    }
}