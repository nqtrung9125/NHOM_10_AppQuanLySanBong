package com.nhom10.appquanlysanbong.ui.common

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nhom10.appquanlysanbong.R

class WorkspaceContent(private val parent: LinearLayout) {
    private val context = parent.context

    fun heading(text: String, size: Float = 20f) = text(text, size, R.color.text_primary, bold = true)

    fun paragraph(text: String) = text(text, 14f, R.color.text_muted)

    fun badge(text: String) {
        val badge = label(text, 12f, R.color.brand, bold = true).apply {
            setBackgroundResource(R.drawable.bg_pill)
            tintBackground(R.color.brand_tint)
            setPadding(dp(10), dp(4), dp(10), dp(4))
        }
        parent.addView(badge, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            bottomMargin = dp(8)
        })
    }

    fun emptyState(message: String) {
        add(label(message, 14f, R.color.text_muted).apply {
            setBackgroundResource(R.drawable.bg_card)
            setPadding(dp(16), dp(14), dp(16), dp(14))
        })
    }

    fun notice(message: String) {
        add(label(message, 14f, R.color.warning_text).apply {
            setBackgroundResource(R.drawable.bg_card)
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.warning_bg))
            setPadding(dp(16), dp(14), dp(16), dp(14))
        })
    }

    fun valueRow(title: String, value: String) {
        val row = LinearLayout(context).apply {
            isBaselineAligned = false
            orientation = LinearLayout.HORIZONTAL
            addView(label(title, 14f, R.color.text_muted), LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
            addView(label(value, 14f, R.color.text_primary, bold = true).apply {
                gravity = Gravity.END
                setPaddingRelative(dp(8), 0, 0, 0)
            }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.35f))
        }
        add(row, gap = 8)
    }

    fun card(title: String, content: WorkspaceContent.() -> Unit) {
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(10))
            setBackgroundResource(R.drawable.bg_card)
        }
        add(container)
        WorkspaceContent(container).apply {
            text(title, 17f, R.color.text_primary, bold = true)
            content()
        }
    }

    fun button(label: String, primary: Boolean = false, action: () -> Unit) {
        add(AppCompatButton(context).apply {
            text = label
            isAllCaps = false
            minHeight = dp(48)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            setBackgroundResource(if (primary) R.drawable.bg_button_primary else R.drawable.bg_guest_outline)
            setTextColor(ContextCompat.getColor(context, if (primary) android.R.color.white else R.color.brand))
            setOnClickListener { action() }
        })
    }

    fun input(label: String, value: String = "", numeric: Boolean = false): AppCompatEditText {
        text(label, 14f, R.color.text_primary, bold = true)
        return AppCompatEditText(context).apply {
            hint = label
            setText(value)
            setSingleLine()
            inputType = if (numeric) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_CLASS_TEXT
            minHeight = dp(48)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setBackgroundResource(R.drawable.bg_input)
            add(this)
        }
    }

    fun choice(label: String, options: List<String>, selected: Int = 0): Spinner {
        text(label, 14f, R.color.text_primary, bold = true)
        return Spinner(context).apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, options)
            if (options.isNotEmpty()) setSelection(selected)
            minimumHeight = dp(48)
            contentDescription = label
            setBackgroundResource(R.drawable.bg_staff_select)
            setPaddingRelative(dp(12), 0, dp(36), 0)
            add(this)
        }
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false) {
        add(label(value, size, color, bold), gap = 8)
    }

    private fun label(value: String, size: Float, color: Int, bold: Boolean = false) =
        TextView(context).apply {
            text = value
            textSize = size
            setTextColor(ContextCompat.getColor(context, color))
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }

    internal fun add(view: android.view.View, gap: Int = 12) {
        parent.addView(view, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            bottomMargin = dp(gap)
        })
    }

    private fun dp(value: Int) = (value * context.resources.displayMetrics.density).toInt()
}
