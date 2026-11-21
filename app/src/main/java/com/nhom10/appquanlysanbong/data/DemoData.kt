package com.nhom10.appquanlysanbong.data

/** Dữ liệu tĩnh cho các màn minh họa. Lịch đặt sân thực tế của bản demo nằm ở BookingRepository. */
object DemoData {
    const val PHONE = "0901 234 567"
    const val PASSWORD = "123456"
    const val OTP_PREFILL = "482"
    const val OTP_RESEND_SECONDS = 42

    // KH-03 Đăng ký
    val LEVELS = listOf("Vui vẻ", "Trung bình", "Khá", "Mạnh")
    const val DEFAULT_LEVEL = 1

    // KH-04 Danh sách sân
    const val USER_NAME = "Trung"
    const val WEATHER_SUMMARY = "Đà Nẵng · 28°C · Chiều mưa 70%"
    const val MATCHES_WAITING = 12

    const val DEPOSIT_PERCENT = 30

    /** Mã QR mẫu 21x21 lấy từ Figma ('1' = ô đen). */
    val QR_SAMPLE = listOf(
        "111111100111101111111",
        "100000100100101000001",
        "101110100011101011101",
        "101110101001001011101",
        "101110100111101011101",
        "100000100001001000001",
        "111111100110001111111",
        "000000001001100000000",
        "111000110010011001001",
        "010110010001000010111",
        "110000111111011001110",
        "000011010011110101001",
        "010010110101101110100",
        "000000001110010000110",
        "111111100101000101000",
        "100000100010000011110",
        "101110101100110000011",
        "101110101110111111110",
        "101110100111110101010",
        "100000101000100001110",
        "111111101101100000011"
    )

    // KH-08 Lịch sử đặt sân
    val bookings = mutableListOf(
        Booking(
            "BK1", "Sân A1 · Cầu lông", "Thứ Năm, 15/10/2026", "17:00 – 19:00", "500.000đ",
            "Đã cọc 150.000đ · Còn lại 350.000đ tại sân", BookingStatus.DEPOSIT_PAID,
            weatherAlert = "Cảnh báo mưa 80% khung 17:00 – 19:00", canCancel = true
        ),
        Booking(
            "BK2", "Sân B1 · Pickleball", "Thứ Hai, 19/10/2026", "18:00 – 19:30", "675.000đ",
            "Cọc 202.500đ · Hạn thanh toán còn 14:32", BookingStatus.PENDING_DEPOSIT,
            payAmount = "202.500đ"
        ),
        Booking(
            "BK3", "Sân A3 · Cầu lông", "Thứ Sáu, 16/10/2026", "18:00 – 20:00", "400.000đ",
            "Đã cọc 120.000đ · Còn lại 280.000đ tại sân", BookingStatus.DEPOSIT_PAID,
            canPass = true
        ),
        Booking(
            "BK4", "Sân A2 · Cầu lông", "Chủ Nhật, 11/10/2026", "16:00 – 17:00", "300.000đ",
            "Đã khấu trừ cọc 90.000đ · Thu tại sân 210.000đ", BookingStatus.COMPLETED
        ),
        Booking(
            "BK5", "Sân A1 · Cầu lông", "Thứ Bảy, 10/10/2026", "19:00 – 20:00", "250.000đ",
            "Hủy hợp lệ · Hoàn lại 75.000đ tiền cọc", BookingStatus.REFUNDED
        ),
        Booking(
            "BK6", "Sân B1 · Pickleball", "Thứ Năm, 08/10/2026", "18:00 – 19:00", "450.000đ",
            "Không đến sau 15 phút · Mất cọc 135.000đ", BookingStatus.FORFEITED
        ),
        Booking(
            "BK7", "Sân A2 · Cầu lông", "Thứ Ba, 06/10/2026", "20:00 – 21:00", "300.000đ",
            "Quá hạn thanh toán cọc · Tự động hủy", BookingStatus.CANCELLED
        )
    )
    // KH-09 Hủy booking và hoàn cọc
    val cancellationInfo = CancellationInfo(
        bookingCode = "DS2026101500012",
        courtName = "Sân A1 · Cầu lông",
        dateTime = "Thứ Năm, 15/10/2026 · 17:00 – 19:00",
        depositAmount = "150.000đ",
        refundAmount = "150.000đ",
        refundMethod = "Phương thức thanh toán ban đầu",
        refundPolicy = "Tiền cọc sẽ được hoàn lại sau khi yêu cầu hủy được xác nhận."
    )

    // KH-10 Thông báo
    val notifications = listOf(
        AppNotification(
            "Cảnh báo thời tiết",
            "Dự báo mưa lớn 80% khung 17:00 – 19:00 hôm nay. Bấm để xem đề xuất dời lịch miễn phí.",
            "2 phút trước",
            "☀",
            true
        ),
        AppNotification(
            "Thanh toán cọc thành công",
            "Bạn đã thanh toán cọc 150.000đ cho booking DS2026101500012. Xuất trình mã QR khi nhận sân.",
            "5 phút trước",
            "✓",
            true
        ),
        AppNotification(
            "Sắp hết giờ thuê sân",
            "Phiếu DS2026101000008 (Sân A1) sẽ hết giờ lúc 19:00.",
            "20 phút trước",
            "◷",
            false
        ),
        AppNotification(
            "Hoàn cọc thành công",
            "Bạn đã được hoàn 75.000đ tiền cọc cho booking DS2026101000005.",
            "Hôm qua",
            "₫",
            false
        ),
        AppNotification(
            "Booking đã tự động hủy",
            "Booking DS2026100800003 đã bị hủy do quá hạn thanh toán cọc.",
            "2 ngày trước",
            "!",
            false
        ),
        AppNotification(
            "Pass sân thành công",
            "Yêu cầu pass sân của bạn đã được xác nhận.",
            "Vừa xong",
            "↗",
            false
        )
    )

    // KH-11 Tìm trận, tìm bạn chơi
    val matchItems = listOf(
        MatchItem(
            court = "Sân A2 · Cầu lông",
            sport = "Cầu lông",
            level = "Vui vẻ",
            schedule = "Hôm nay · 18:00 – 19:00 · cách 1,2 km",
            players = "2/4 người",
            missing = "Còn thiếu 2",
            price = "50.000đ/người"
        ),
        MatchItem(
            court = "Sân B1 · Pickleball",
            sport = "Pickleball",
            level = "Trung bình",
            schedule = "Ngày mai · 19:00 – 20:30 · cách 2,8 km",
            players = "2/4 người",
            missing = "Còn thiếu 2",
            price = "≈ 48.000đ/người"
        ),
        MatchItem(
            court = "Sân A1 · Cầu lông",
            sport = "Cầu lông",
            level = "Khá",
            schedule = "Thứ Sáu · 20:00 – 21:00 · cách 3,5 km",
            players = "3/4 người",
            missing = "Còn thiếu 1",
            price = "25.000đ/người"
        )
    )
}
