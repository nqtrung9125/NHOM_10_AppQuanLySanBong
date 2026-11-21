package com.nhom10.appquanlysanbong.ui.common

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.ViewBottomNavBinding
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment
import com.nhom10.appquanlysanbong.ui.history.BookingHistoryFragment
import com.nhom10.appquanlysanbong.ui.match.FindMatchFragment
import com.nhom10.appquanlysanbong.ui.account.AccountFragment


enum class NavTab { HOME, MATCH, HISTORY, ACCOUNT }

/** Thanh điều hướng dưới dùng chung: tô tab đang chọn và xử lý bấm tab. */
object BottomNav {

    private class Item(val tab: NavTab, val box: View, val icon: ImageView, val label: TextView)

    fun setup(nav: ViewBottomNavBinding, selected: NavTab, navigator: Navigator) {
        val ctx = nav.root.context
        val brand = ContextCompat.getColor(ctx, R.color.brand)
        val muted = ContextCompat.getColor(ctx, R.color.text_muted)
        val ripple = TypedValue().also {
            ctx.theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true)
        }.resourceId

        val items = listOf(
            Item(NavTab.HOME, nav.navHome, nav.icHome, nav.tvHome),
            Item(NavTab.MATCH, nav.navMatch, nav.icMatch, nav.tvMatch),
            Item(NavTab.HISTORY, nav.navHistory, nav.icHistory, nav.tvHistory),
            Item(NavTab.ACCOUNT, nav.navAccount, nav.icAccount, nav.tvAccount)
        )

        items.forEach { item ->
            val on = item.tab == selected
            if (on) {
                item.box.setBackgroundResource(R.drawable.bg_card)
                item.box.tintBackground(R.color.brand_tint)
            } else {
                item.box.setBackgroundResource(ripple)
                item.box.tintBackground(null)
            }
            item.icon.imageTintList = ColorStateList.valueOf(if (on) brand else muted)
            item.label.setTextColor(if (on) brand else muted)
            item.label.typeface =
                if (on) Typeface.create("sans-serif-medium", Typeface.NORMAL) else Typeface.DEFAULT
            item.box.setOnClickListener { if (!on) onTabClick(item.tab, navigator) }
        }
    }

    private fun onTabClick(tab: NavTab, navigator: Navigator) {
        when (tab) {
            NavTab.HOME ->
                navigator.navigateAsRoot(CourtListFragment())

            NavTab.HISTORY ->
                navigator.navigateAsRoot(BookingHistoryFragment())

            NavTab.MATCH ->
                navigator.navigateAsRoot(FindMatchFragment())

            NavTab.ACCOUNT ->
                navigator.navigateAsRoot(AccountFragment())
        }
    }
}
