package com.nhom10.appquanlysanbong.ui.admin

import com.nhom10.appquanlysanbong.data.session.Actor
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent

/** Danh sách tài khoản hiển thị vai trò hiện tại của từng người. */
internal fun WorkspaceContent.userCards(actors: List<Actor>) {
    if (actors.isEmpty()) emptyState("Chưa tìm thấy tài khoản phù hợp.")
    actors.forEach { actor ->
        card(actor.name) {
            valueRow("Số điện thoại", actor.phone)
            badge(actor.role.label)
        }
    }
}
