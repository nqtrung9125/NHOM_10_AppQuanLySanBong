package com.nhom10.appquanlysanbong.ui.common

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.formatVndAmount


// Đổi màu nền View
fun View.tintBackground(colorRes: Int?) {
    backgroundTintList = colorRes?.let {
        ContextCompat.getColorStateList(context, it)
    }
}

// Định dạng tiền Việt Nam
fun Int.vnd(): String = "${formatVndAmount(this)} đ"

fun Long.vnd(): String = "${formatVndAmount(this)} đ"

// Thay đổi trạng thái Chip
fun TextView.setChipSelected(selected: Boolean) {
    tintBackground(if (selected) R.color.brand_tint else null)

    setTextColor(
        ContextCompat.getColor(
            context,
            if (selected) R.color.brand else R.color.text_muted
        )
    )

    typeface = if (selected) {
        Typeface.create("sans-serif-medium", Typeface.NORMAL)
    } else {
        Typeface.DEFAULT
    }
}

// Hiển thị thông báo chức năng sắp ra mắt
fun Fragment.toastComingSoon() {
    Toast.makeText(
        requireContext(),
        R.string.msg_coming_soon,
        Toast.LENGTH_SHORT
    ).show()
}
