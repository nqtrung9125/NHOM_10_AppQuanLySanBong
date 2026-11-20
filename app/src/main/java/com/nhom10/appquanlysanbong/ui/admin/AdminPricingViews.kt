package com.nhom10.appquanlysanbong.ui.admin

import com.nhom10.appquanlysanbong.data.Court
import com.nhom10.appquanlysanbong.data.courts.displayName
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent

/** Các thẻ giá dùng cùng dữ liệu với màn xem sân và đặt sân. */
internal fun WorkspaceContent.pricingCards(courts: List<Court>, weekend: Boolean, edit: (Court) -> Unit) {
    if (courts.isEmpty()) emptyState("Chưa có bảng giá cho môn này.")
    courts.forEach { court ->
        card(court.displayName) {
            valueRow("05:00 – 16:00", court.prices[0].price)
            valueRow("16:00 – 22:00", court.prices[if (weekend) 2 else 1].price)
            button("Chỉnh giá") { edit(court) }
        }
    }
    notice("Giá mới áp dụng khi tạo phiếu đặt tiếp theo. Phiếu đã tạo giữ nguyên số tiền.")
}
