package com.nhom10.appquanlysanbong.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.nhom10.appquanlysanbong.databinding.ItemQlMetricBinding
import com.nhom10.appquanlysanbong.databinding.ItemQlStatBinding
import kotlin.math.roundToInt

internal data class DashboardStat(val value: String, val label: String, val spokenValue: String? = null)

/** Hai thành phần từ dashboard Downloads. Fragment truyền số liệu đã tính từ repository. */
internal fun WorkspaceContent.statistics(context: Context, vararg stats: DashboardStat) {
    val inflater = LayoutInflater.from(context)
    val columns = if (context.resources.configuration.fontScale > 1.3f) 2 else 3
    stats.toList().chunked(columns).forEach { items ->
        val row = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
        items.forEachIndexed { index, stat ->
            val tile = ItemQlStatBinding.inflate(inflater, row, false)
            tile.qlStatValue.text = stat.value
            tile.qlStatValue.contentDescription = stat.spokenValue
            tile.qlStatLabel.text = stat.label
            if (index > 0) {
                (tile.root.layoutParams as LinearLayout.LayoutParams).marginStart =
                    (8 * context.resources.displayMetrics.density).roundToInt()
            }
            row.addView(tile.root)
        }
        add(row)
    }
}

internal fun WorkspaceContent.occupancy(context: Context, court: String, percent: Int) {
    val row = ItemQlMetricBinding.inflate(LayoutInflater.from(context))
    row.qlMetricLabel.text = court
    row.qlMetricValue.text = "$percent%"
    row.qlMetricProgress.progress = percent
    add(row.root)
}
