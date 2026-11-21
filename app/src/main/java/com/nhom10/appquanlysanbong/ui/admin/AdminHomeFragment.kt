package com.nhom10.appquanlysanbong.ui.admin

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.data.session.DemoAccountRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.databinding.ItemCourtBinding
import com.nhom10.appquanlysanbong.ui.common.DashboardStat
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment
import com.nhom10.appquanlysanbong.ui.common.filters
import com.nhom10.appquanlysanbong.ui.common.statistics
import com.nhom10.appquanlysanbong.ui.common.tintBackground

class AdminHomeFragment : WorkspaceFragment() {
    override val role = Role.ADMIN
    override val home = true
    protected override val tabs = Page.entries.map { StaffTab(it.label, it.icon) }
    private val page get() = Page.entries[selectedTab]
    override val title get() = page.title
    private val sports = listOf(null) + Sport.entries
    private var sportIndex = 0
    private var pricingSportIndex = 0
    private var weekend = false
    private var userRoleIndex = 0
    private var userSearch = ""
    private val userRoles = listOf(null) + Role.entries.filter { it != Role.GUEST }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sportIndex = savedInstanceState?.getInt("court_sport")?.takeIf { it in sports.indices } ?: sportIndex
        pricingSportIndex = savedInstanceState?.getInt("pricing_sport") ?: pricingSportIndex
        weekend = savedInstanceState?.getBoolean("pricing_weekend") ?: weekend
        userRoleIndex = savedInstanceState?.getInt("user_role") ?: userRoleIndex
        userSearch = savedInstanceState?.getString("user_search") ?: userSearch
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("court_sport", sportIndex)
        outState.putInt("pricing_sport", pricingSportIndex)
        outState.putBoolean("pricing_weekend", weekend)
        outState.putInt("user_role", userRoleIndex)
        outState.putString("user_search", userSearch)
    }

    override fun WorkspaceContent.content() {
        val context = requireContext()
        when (page) {
            Page.COURTS -> courts(CourtCatalog(context).all())
            Page.PRICING -> pricing(CourtCatalog(context).all())
            Page.USERS -> users()
            Page.PROFILE -> staffAccount {
                val courts = CourtCatalog(context).all()
                statistics(context,
                    DashboardStat(DemoAccountRepository(context).all().size.toString(), "Tài khoản"),
                    DashboardStat(courts.size.toString(), "Tổng số sân"),
                    DashboardStat(courts.count { it.available }.toString(), "Đang mở")
                )
            }
        }
    }

    private fun WorkspaceContent.pricing(all: List<Court>) {
        val context = requireContext()
        val pricingSports = listOf(Sport.BADMINTON, Sport.PICKLEBALL, Sport.FOOTBALL, Sport.VOLLEYBALL)
        filters(context, pricingSports.map { it.label }, pricingSportIndex) {
            pricingSportIndex = it
            render()
        }
        filters(context, listOf("Ngày thường", "Cuối tuần"), if (weekend) 1 else 0) {
            weekend = it == 1
            render()
        }
        pricingCards(all.filter { it.sport == pricingSports[pricingSportIndex] }, weekend) {
            navigator.navigateTo(EditCourtFragment.forPricing(it.id))
        }
    }

    private fun WorkspaceContent.users() {
        val context = requireContext()
        val search = input("Tìm theo tên hoặc số điện thoại", userSearch)
        button("Tìm tài khoản") {
            userSearch = search.text.toString().trim()
            render()
        }
        filters(context, userRoles.map { it?.label ?: "Tất cả" }, userRoleIndex) {
            userRoleIndex = it
            userSearch = search.text.toString().trim()
            render()
        }
        val selected = userRoles[userRoleIndex]
        userCards(DemoAccountRepository(context).all().filter {
            (selected == null || it.role == selected) &&
                (it.name.contains(userSearch, ignoreCase = true) || it.phone.contains(userSearch))
        })
    }

    private fun WorkspaceContent.courts(all: List<Court>) {
        val context = requireContext()
        paragraph("${all.size} sân · ${all.count { it.available }} đang hoạt động")
        filters(context, sports.map { it?.label ?: "Tất cả" }, sportIndex) {
            sportIndex = it
            render()
        }
        button("Thêm sân mới", primary = true) { navigator.navigateTo(EditCourtFragment()) }
        val visible = all.filter { sports[sportIndex] == null || it.sport == sports[sportIndex] }
        if (visible.isEmpty()) paragraph("Chưa có sân thuộc môn này.")
        visible.forEach { court ->
            val screen = ItemCourtBinding.inflate(layoutInflater)
            screen.ivCourtArt.setImageResource(R.drawable.ic_guest_court)
            screen.tvCourtName.text = court.displayName
            screen.tvCourtDesc.text = court.description
            screen.tvCourtPrice.text = court.priceFrom
            screen.tvCourtStatus.setText(if (court.available) R.string.status_available else R.string.status_in_use)
            screen.tvCourtStatus.tintBackground(if (court.available) R.color.brand_tint else R.color.danger_bg)
            screen.tvCourtStatus.setTextColor(ContextCompat.getColor(context,
                if (court.available) R.color.brand else R.color.danger))
            screen.root.setOnClickListener { navigator.navigateTo(EditCourtFragment.forId(court.id)) }
            add(screen.root)
        }
    }

    private enum class Page(val label: String, val title: String, val icon: Int) {
        COURTS("Sân", "Quản lý sân", R.drawable.ic_ball),
        PRICING("Bảng giá", "Bảng giá theo sân", R.drawable.ic_info),
        USERS("Người dùng", "Tài khoản và vai trò", R.drawable.ic_people),
        PROFILE("Cá nhân", "Tài khoản quản trị", R.drawable.ic_person)
    }
}
