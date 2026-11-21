package com.nhom10.appquanlysanbong.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.nhom10.appquanlysanbong.R

/** Vẽ mã QR mẫu từ ma trận chuỗi '0'/'1' (chỉ để demo, chưa phải QR thật). */
class QrCodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_primary)
    }
    private var rows: List<String> = emptyList()

    fun setMatrix(rows: List<String>) {
        this.rows = rows
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rows.isEmpty()) return
        val cell = minOf(width, height).toFloat() / rows.size
        rows.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '1') {
                    canvas.drawRect(x * cell, y * cell, (x + 1) * cell, (y + 1) * cell, paint)
                }
            }
        }
    }
}