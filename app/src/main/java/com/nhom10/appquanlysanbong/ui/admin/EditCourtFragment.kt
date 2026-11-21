package com.nhom10.appquanlysanbong.ui.admin

import android.os.Bundle
import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.courts.CourtCatalog
import com.nhom10.appquanlysanbong.data.courts.CourtEdit
import com.nhom10.appquanlysanbong.data.courts.toEdit
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment

class EditCourtFragment : WorkspaceFragment() {
    private val pricingOnly get() = arguments?.getBoolean("pricing_only") == true
    override val title get() = when {
        pricingOnly -> "Chỉnh bảng giá"
        arguments == null -> "Thêm sân"
        else -> "Thiết lập sân"
    }
    override val role = Role.ADMIN

    override fun WorkspaceContent.content() {
        val catalog = CourtCatalog(requireContext())
        val current = catalog.find(arguments?.getString("id"))?.toEdit()
        if (pricingOnly) {
            if (current == null) emptyState("Sân này không còn trong danh mục.")
            else priceEditor(catalog, current)
            return
        }
        val id = input("Mã sân", current?.id.orEmpty()).apply { isEnabled = current == null }
        val name = input("Tên sân", current?.name.orEmpty())
        val sports = Sport.entries
        val sport = choice("Môn", sports.map { it.label }, current?.sport?.ordinal ?: 0)
        val count = input("Sức chứa / Cỡ sân bóng đá", current?.players?.toString().orEmpty(), numeric = true)
        val day = input("Giá trước 16:00 (đ/giờ)", current?.day?.toString().orEmpty(), numeric = true)
        val evening = input("Giá tối ngày thường (đ/giờ)", current?.evening?.toString().orEmpty(), numeric = true)
        val weekend = input("Giá tối cuối tuần (đ/giờ)", current?.weekend?.toString().orEmpty(), numeric = true)
        val state = choice("Trạng thái", listOf("Hoạt động", "Tạm ngưng"), if (current?.active == false) 1 else 0)
        notice("Kiểm tra mã sân, sức chứa và ba mức giá trước khi lưu.")
        button("Lưu thông tin sân", primary = true) {
            perform("Đã lưu sân.") {
                val key = id.text.toString().trim().uppercase()
                require(current != null || catalog.find(key) == null) { "Mã sân đã được dùng." }
                catalog.save(CourtEdit(key, name.text.toString().trim(), sports[sport.selectedItemPosition],
                    count.text.toString().toIntOrNull() ?: 0, day.text.toString().toIntOrNull() ?: 0,
                    evening.text.toString().toIntOrNull() ?: 0, weekend.text.toString().toIntOrNull() ?: 0,
                    state.selectedItemPosition == 0))
                closeEditor()
            }
        }
    }

    private fun WorkspaceContent.priceEditor(catalog: CourtCatalog, current: CourtEdit) {
        heading("${current.name} · ${current.sport.label}")
        val day = input("05:00 – 16:00 (đ/giờ)", current.day.toString(), numeric = true)
        val evening = input("16:00 – 22:00 ngày thường (đ/giờ)", current.evening.toString(), numeric = true)
        val weekend = input("16:00 – 22:00 cuối tuần (đ/giờ)", current.weekend.toString(), numeric = true)
        notice("Giá mới áp dụng khi tạo phiếu đặt tiếp theo.")
        button("Lưu bảng giá", primary = true) {
            perform("Đã lưu bảng giá.") {
                val latest = requireNotNull(catalog.find(current.id)?.toEdit()) { "Không tìm thấy sân." }
                catalog.save(latest.copy(
                    day = day.text.toString().toIntOrNull() ?: 0,
                    evening = evening.text.toString().toIntOrNull() ?: 0,
                    weekend = weekend.text.toString().toIntOrNull() ?: 0
                ))
                closeEditor()
            }
        }
    }

    private fun closeEditor() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    companion object {
        fun forId(id: String) = EditCourtFragment().apply {
            arguments = Bundle().apply { putString("id", id) }
        }

        fun forPricing(id: String) = forId(id).apply {
            requireArguments().putBoolean("pricing_only", true)
        }
    }
}
