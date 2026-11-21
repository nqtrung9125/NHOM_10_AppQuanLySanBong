package com.nhom10.appquanlysanbong.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.ItemQlMenuBinding
import kotlin.math.roundToInt

internal data class StaffAction(val title: String, val icon: Int, val select: () -> Unit)

/** Dòng thao tác dạng thẻ kế thừa bố cục ở bản Downloads. */
internal fun WorkspaceContent.actions(context: Context, title: String, vararg items: StaffAction) {
    heading(title)
    val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundResource(R.drawable.bg_card)
        clipToOutline = true
    }
    val inflater = LayoutInflater.from(context)
    items.forEachIndexed { index, action ->
        if (index > 0) container.addView(View(context).apply {
            layoutParams = LinearLayout.LayoutParams(-1, context.staffDp(1))
            setBackgroundColor(ContextCompat.getColor(context, R.color.border_card))
        })
        val row = ItemQlMenuBinding.inflate(inflater, container, false)
        row.qlMenuIcon.setImageResource(action.icon)
        row.qlMenuIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brand))
        row.qlMenuLabel.text = action.title
        row.root.setOnClickListener { action.select() }
        container.addView(row.root)
    }
    add(container)
}

internal fun WorkspaceContent.filters(
    context: Context, labels: List<String>, current: Int, select: (Int) -> Unit
) {
    val row = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
    val scroll = HorizontalScrollView(context).apply {
        isHorizontalScrollBarEnabled = false
        addView(row)
    }
    labels.forEachIndexed { index, label ->
        val active = current == index
        val chip = AppCompatButton(context).apply {
            text = label
            isAllCaps = false
            isSelected = active
            minHeight = context.staffDp(48)
            setPadding(context.staffDp(12), 0, context.staffDp(12), 0)
            setBackgroundResource(R.drawable.bg_filter)
            tintBackground(if (active) R.color.brand else null)
            setTextColor(ContextCompat.getColor(context, if (active) android.R.color.white else R.color.text_primary))
            setOnClickListener { select(index) }
        }
        row.addView(chip, LinearLayout.LayoutParams(-2, -2).apply {
            if (index > 0) marginStart = context.staffDp(8)
        })
        if (active) scroll.post {
            if (chip.right > scroll.width) scroll.scrollTo(chip.right - scroll.width, 0)
        }
    }
    add(scroll)
}

private fun Context.staffDp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
