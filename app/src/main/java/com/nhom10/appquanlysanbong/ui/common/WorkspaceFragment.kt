package com.nhom10.appquanlysanbong.ui.common

import android.os.Bundle
import android.content.res.ColorStateList
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.data.session.SessionStore
import com.nhom10.appquanlysanbong.databinding.FragmentWorkspaceBinding
import com.nhom10.appquanlysanbong.databinding.ItemStaffTabBinding
import com.nhom10.appquanlysanbong.databinding.ViewStaffProfileBinding
import com.nhom10.appquanlysanbong.ui.auth.openActorHome

/** Khung XML dùng chung. Mỗi Fragment quyết định nội dung và thao tác của màn hình. */
abstract class WorkspaceFragment : Fragment(R.layout.fragment_workspace) {
    protected data class StaffTab(val label: String, val icon: Int)

    protected abstract val title: String
    protected abstract val role: Role
    protected open val home = false
    protected open val tabs: List<StaffTab> = emptyList()
    protected var selectedTab: Int = 0
        private set
    protected val navigator get() = requireActivity() as Navigator
    protected val session get() = SessionStore(requireContext())
    private var binding: FragmentWorkspaceBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentWorkspaceBinding.bind(view)
        savedInstanceState?.getInt("staff_tab")?.takeIf { it in tabs.indices }?.let { selectedTab = it }
        val screen = requireNotNull(binding)
        screen.workspaceTabs.isVisible = tabs.isNotEmpty()
        tabs.forEachIndexed { index, tab ->
            val item = ItemStaffTabBinding.inflate(layoutInflater, screen.workspaceTabs, false)
            item.staffTabIcon.setImageResource(tab.icon)
            item.staffTabLabel.text = tab.label
            item.root.contentDescription = tab.label
            item.root.setOnClickListener { openTab(index) }
            screen.workspaceTabs.addView(item.root)
        }
        binding?.appbar?.apply {
            tvAppbarTitle.text = title
            btnBack.visibility = if (home) View.GONE else View.VISIBLE
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
        render()
    }

    protected abstract fun WorkspaceContent.content()

    protected fun render() {
        val screen = binding ?: return
        if (session.current.role != role) {
            openActorHome()
            return
        }
        screen.appbar.tvAppbarTitle.text = title
        for (index in 0 until screen.workspaceTabs.childCount) {
            val item = ItemStaffTabBinding.bind(screen.workspaceTabs.getChildAt(index))
            val active = index == selectedTab
            val textColor = ContextCompat.getColor(requireContext(), if (active) R.color.brand else R.color.text_muted)
            item.root.isSelected = active
            item.staffTabIcon.imageTintList = ColorStateList.valueOf(textColor)
            item.staffTabLabel.setTextColor(textColor)
        }
        screen.workspaceContent.removeAllViews()
        WorkspaceContent(screen.workspaceContent).content()
    }

    protected fun openTab(index: Int) {
        if (index == selectedTab || index !in tabs.indices) return
        selectedTab = index
        render()
        binding?.workspaceScroll?.scrollTo(0, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("staff_tab", selectedTab)
    }

    protected fun perform(message: String, action: () -> Unit) {
        val context = requireContext()
        try {
            action()
            if (message.isNotBlank()) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            render()
        } catch (error: IllegalArgumentException) {
            Toast.makeText(context, error.message ?: "Dữ liệu chưa hợp lệ.", Toast.LENGTH_LONG).show()
        }
    }

    protected fun confirm(title: String, message: String, action: () -> Unit) {
        AlertDialog.Builder(requireContext()).setTitle(title).setMessage(message)
            .setNegativeButton("Quay lại", null)
            .setPositiveButton("Xác nhận") { _, _ -> action() }.show()
    }

    protected fun WorkspaceContent.staffAccount(secondary: WorkspaceContent.() -> Unit = {}) {
        val actor = session.current
        val profile = ViewStaffProfileBinding.inflate(layoutInflater)
        profile.staffInitial.text = actor.name.substringAfterLast(' ').take(1).uppercase()
        profile.staffName.text = actor.name
        profile.staffPhone.text = actor.phone
        profile.staffRole.text = actor.role.label
        add(profile.root, gap = 16)
        secondary()
        actions(requireContext(), "Tài khoản",
            StaffAction("Đăng xuất", R.drawable.ic_lt_logout) {
                session.signOut()
                openActorHome()
            }
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
