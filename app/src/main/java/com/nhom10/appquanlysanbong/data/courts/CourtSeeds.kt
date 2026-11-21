package com.nhom10.appquanlysanbong.data.courts

import com.nhom10.appquanlysanbong.data.Sport

// Giá theo thứ tự trước 16:00, tối ngày thường, tối cuối tuần.
internal val demoCourts = listOf(
    newCourt("A1", "Sân A1", Sport.BADMINTON, 4, 150_000, 250_000, 300_000, true),
    newCourt("A2", "Sân A2", Sport.BADMINTON, 4, 150_000, 250_000, 300_000, false),
    newCourt("A3", "Sân A3", Sport.BADMINTON, 4, 150_000, 250_000, 300_000, true),
    newCourt("B1", "Sân B1", Sport.PICKLEBALL, 4, 300_000, 350_000, 400_000, true),
    newCourt("B2", "Sân B2", Sport.PICKLEBALL, 4, 300_000, 350_000, 400_000, true),
    newCourt("C1", "Sân C1", Sport.VOLLEYBALL, 12, 800_000, 800_000, 800_000, true),
    newCourt("D1", "Sân D1", Sport.FOOTBALL, 5, 350_000, 500_000, 500_000, true),
    newCourt("D2", "Sân D2", Sport.FOOTBALL, 7, 550_000, 800_000, 800_000, true),
    newCourt("D3", "Sân D3", Sport.FOOTBALL, 11, 900_000, 1_200_000, 1_200_000, true)
)
