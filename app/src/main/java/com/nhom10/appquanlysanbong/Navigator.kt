package com.nhom10.appquanlysanbong

import androidx.fragment.app.Fragment

/** Fragment gọi interface này để chuyển màn hình. */
interface Navigator {
    fun navigateTo(fragment: Fragment, addToBackStack: Boolean = true)

    /** Chuyển sang màn gốc mới và xóa hết back stack (dùng sau khi đăng nhập). */
    fun navigateAsRoot(fragment: Fragment)

    /** Màn nền sáng (như KH-07) gọi true để thanh trạng thái đổi sang sáng. */
    fun setStatusBarLight(light: Boolean)
}