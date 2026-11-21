package com.nhom10.appquanlysanbong.ui.reception

import com.nhom10.appquanlysanbong.data.matchmaking.MatchRequestState
import com.nhom10.appquanlysanbong.data.matchmaking.MatchmakingRepository
import com.nhom10.appquanlysanbong.data.session.Role
import com.nhom10.appquanlysanbong.ui.common.WorkspaceContent
import com.nhom10.appquanlysanbong.ui.common.WorkspaceFragment

class ReceptionMatchmakingFragment : WorkspaceFragment() {
    override val title = "Yêu cầu cáp kèo"
    override val role = Role.RECEPTION

    override fun WorkspaceContent.content() {
        val repository = MatchmakingRepository(requireContext())
        val requests = repository.requests()
        if (requests.isEmpty()) emptyState("Chưa có yêu cầu cáp kèo.")
        requests.forEach { request ->
            card("${request.customer} · ${request.sport.label}") {
                badge(request.state.label)
                valueRow("Mong muốn", request.preference)
                if (request.proposal.isNotBlank()) valueRow("Đề xuất đã gửi", request.proposal)
                if (request.state == MatchRequestState.WAITING) {
                    val proposal = input("Đối thủ, sân và giờ chơi")
                    button("Gửi đề xuất", primary = true) {
                        perform("Đã gửi đề xuất cho khách.") {
                            repository.propose(request.id, proposal.text.toString())
                        }
                    }
                }
            }
        }
    }
}
