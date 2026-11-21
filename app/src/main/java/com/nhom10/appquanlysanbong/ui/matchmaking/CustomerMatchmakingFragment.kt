package com.nhom10.appquanlysanbong.ui.matchmaking

import com.nhom10.appquanlysanbong.data.Sport
import com.nhom10.appquanlysanbong.data.matchmaking.MatchRequestState
import com.nhom10.appquanlysanbong.data.matchmaking.MatchmakingRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment

class CustomerMatchmakingFragment : WorkspaceFragment() {
    override val title = "Nhờ nhân viên cáp kèo"
    override val role = Role.CUSTOMER

    override fun WorkspaceContent.content() {
        val repository = MatchmakingRepository(requireContext())
        notice("Lễ tân đề xuất đối phù hợp. Gửi yêu cầu chưa phát sinh tiền cọc.")
        card("Gửi yêu cầu") {
            val sports = listOf(Sport.FOOTBALL, Sport.BADMINTON, Sport.PICKLEBALL)
            val sport = choice("Môn thể thao", sports.map { it.label })
            val preference = input("Ngày, giờ và trình độ mong muốn")
            button("Gửi yêu cầu", primary = true) {
                perform("Đã gửi yêu cầu cho lễ tân.") {
                    repository.create(sports[sport.selectedItemPosition], preference.text.toString())
                }
            }
        }
        heading("Yêu cầu của tôi")
        val requests = repository.mine()
        if (requests.isEmpty()) emptyState("Bạn chưa gửi yêu cầu nào.")
        requests.forEach { request ->
            card(request.sport.label) {
                badge(request.state.label)
                valueRow("Mong muốn", request.preference)
                if (request.proposal.isNotBlank()) valueRow("Đề xuất", request.proposal)
                if (request.state == MatchRequestState.PROPOSED) {
                    button("Đồng ý kèo", primary = true) {
                        perform("Đã xác nhận kèo.") { repository.respond(request.id, true) }
                    }
                    button("Từ chối") {
                        perform("Đã từ chối kèo.") { repository.respond(request.id, false) }
                    }
                }
            }
        }
    }
}
