package com.nhom10.appquanlysanbong.data

data class PassItem(
    val passCode: String,
    val courtName: String,
    val sportLabel: String,
    val date: String,
    val time: String,
    val totalVnd: Int,
    val depositVnd: Int,
    val remainVnd: Int,
    val timerBadge: String,
    val courtType: String,
    val duration: String,
    val poster: String,
    val deadline: String
)

object PassData {
    val samplePasses = listOf(
        PassItem(
            passCode = "PS2026101600004",
            courtName = "Sân A3 · Cầu lông",
            sportLabel = "Cầu lông",
            date = "Thứ Sáu, 16/10/2026",
            time = "18:00 – 20:00",
            totalVnd = 400_000,
            depositVnd = 120_000,
            remainVnd = 280_000,
            timerBadge = "Còn 21 giờ",
            courtType = "Cầu lông · Thảm PVC",
            duration = "2 giờ",
            poster = "Trần M. K.",
            deadline = "16:00 · 16/10/2026"
        ),
        PassItem(
            passCode = "PS2026101700012",
            courtName = "Sân B1 · Pickleball",
            sportLabel = "Pickleball",
            date = "Thứ Bảy, 17/10/2026",
            time = "19:00 – 20:30",
            totalVnd = 675_000,
            depositVnd = 202_500,
            remainVnd = 472_500,
            timerBadge = "Sắp hết hạn",
            courtType = "Pickleball · Sơn acrylic",
            duration = "1.5 giờ",
            poster = "Nguyễn Văn H.",
            deadline = "17:00 · 17/10/2026"
        ),
        PassItem(
            passCode = "PS2026101800002",
            courtName = "Sân A1 · Cầu lông",
            sportLabel = "Cầu lông",
            date = "Chủ Nhật, 18/10/2026",
            time = "17:00 – 18:00",
            totalVnd = 300_000,
            depositVnd = 90_000,
            remainVnd = 210_000,
            timerBadge = "Còn 2 ngày",
            courtType = "Cầu lông · Thảm PVC",
            duration = "1 giờ",
            poster = "Lê Thị T.",
            deadline = "15:00 · 18/10/2026"
        )
    )
}
