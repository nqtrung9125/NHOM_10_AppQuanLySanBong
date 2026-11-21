package com.nhom10.appquanlysanbong.ui.booking

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.BookingStatus
import com.nhom10.appquanlysanbong.databinding.FragmentBookingDetailBinding
import com.nhom10.appquanlysanbong.ui.common.tintBackground
import com.nhom10.appquanlysanbong.ui.common.vnd
import com.nhom10.appquanlysanbong.ui.courts.CourtListFragment
import com.nhom10.appquanlysanbong.ui.history.BookingHistoryFragment

/** KH-20 Chi tiết booking: hiển thị thông tin chi tiết ứng với từng trạng thái đơn đặt sân. */
class BookingDetailFragment : Fragment(R.layout.fragment_booking_detail) {

    private var _binding: FragmentBookingDetailBinding? = null
    private val binding get() = _binding!!

    private var status: BookingStatus = BookingStatus.DEPOSIT_PAID
    private var customCourt: String? = null
    private var customDate: String? = null
    private var customTime: String? = null
    private var customTotal: Int? = null
    private var customDeposit: Int? = null
    private var customCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val statusName = it.getString(ARG_STATUS, BookingStatus.DEPOSIT_PAID.name)
            status = try { BookingStatus.valueOf(statusName) } catch (e: Exception) { BookingStatus.DEPOSIT_PAID }
            customCourt = it.getString(ARG_COURT)
            customDate = it.getString(ARG_DATE)
            customTime = it.getString(ARG_TIME)
            if (it.containsKey(ARG_TOTAL)) customTotal = it.getInt(ARG_TOTAL)
            if (it.containsKey(ARG_DEPOSIT)) customDeposit = it.getInt(ARG_DEPOSIT)
            customCode = it.getString(ARG_CODE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingDetailBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Chi tiết booking"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        renderDetails(navigator)
    }

    private fun getCourtIdFromName(name: String): String {
        return when {
            name.contains("A2") -> "A2"
            name.contains("B1") -> "B1"
            else -> "A1"
        }
    }

    private fun renderDetails(navigator: Navigator) {
        val ctx = requireContext()

        binding.tvStatusBadge.text = status.label
        binding.tvStatusBadge.tintBackground(status.chipBg)
        binding.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, status.chipText))

        when (status) {
            BookingStatus.DEPOSIT_PAID -> {
                binding.tvCourtTitle.text = customCourt ?: "Sân A1 · Cầu lông"
                val dateStr = customDate ?: "Thứ Năm, 15/10/2026"
                val timeStr = customTime ?: "17:00 – 19:00"
                binding.tvBookingDatetime.text = "$dateStr · $timeStr"
                binding.tvBookingCode.text = customCode ?: "DS2026101500012"

                val total = customTotal ?: 500_000
                val deposit = customDeposit ?: 150_000
                val remain = total - deposit

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0E7FF"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C7D2FE"))
                binding.tvBannerIcon.setTextColor(ContextCompat.getColor(ctx, R.color.brand))
                binding.tvBannerText.text = "Bạn đã thanh toán cọc. Khi đến sân, ${deposit.vnd()} tiền cọc được khấu trừ vào tổng tiền sân."
                binding.tvBannerText.setTextColor(Color.parseColor("#3730A3"))

                addPaymentRow("Tổng tiền sân", total.vnd())
                addPaymentRow("Đã thanh toán cọc (30%)", deposit.vnd(), isBrand = true)
                addPaymentRow("Thanh toán lúc", "Vừa xong · Ví điện tử")
                addPaymentRow("Còn lại thanh toán tại sân", remain.vnd(), isBold = true)

                binding.tvFooterNote.visibility = View.VISIBLE
                binding.tvFooterNote.text = "Hủy trước giờ chơi ít nhất 12 giờ để được hoàn 100% tiền cọc."

                binding.btnPrimaryAction.text = "Xem mã QR nhận sân"
                binding.btnPrimaryAction.setOnClickListener {
                    Toast.makeText(ctx, "Mã QR nhận sân: ${customCode ?: "DS2026101500012"}", Toast.LENGTH_SHORT).show()
                }

                binding.btnSecondaryAction.visibility = View.VISIBLE
                binding.btnSecondaryAction.text = "Hủy booking & hoàn cọc"
                binding.btnSecondaryAction.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEE2E2"))
                binding.btnSecondaryAction.setTextColor(Color.parseColor("#DC2626"))
                binding.btnSecondaryAction.setOnClickListener {
                    navigator.navigateTo(CancelBookingFragment())
                }
            }

            BookingStatus.PENDING_DEPOSIT -> {
                binding.tvCourtTitle.text = customCourt ?: "Sân B1 · Pickleball"
                binding.tvBookingDatetime.text = "Thứ Hai, 19/10/2026 · 18:00 – 19:30"
                binding.tvBookingCode.text = "DS2026101900021"

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF3C7"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FDE68A"))
                binding.tvBannerIcon.setTextColor(Color.parseColor("#D97706"))
                binding.tvBannerText.text = "Vui lòng thanh toán cọc 30% trước 09:56 (còn 14:32). Quá hạn, booking sẽ tự động hủy."
                binding.tvBannerText.setTextColor(Color.parseColor("#92400E"))

                addPaymentRow("Tổng tiền sân", "675.000đ")
                addPaymentRow("Cọc cần thanh toán (30%)", "202.500đ", isBrand = true)
                addPaymentRow("Hạn thanh toán", "09:56 · còn 14:32", isWarning = true)
                addPaymentRow("Còn lại thanh toán tại sân", "472.500đ")

                binding.btnPrimaryAction.text = "Thanh toán cọc 202.500đ"
                binding.btnPrimaryAction.setOnClickListener {
                    Toast.makeText(ctx, "Đã thanh toán cọc thành công", Toast.LENGTH_SHORT).show()
                    navigator.navigateAsRoot(CourtListFragment())
                }

                binding.btnSecondaryAction.visibility = View.VISIBLE
                binding.btnSecondaryAction.text = "Hủy booking"
                binding.btnSecondaryAction.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEE2E2"))
                binding.btnSecondaryAction.setTextColor(Color.parseColor("#DC2626"))
                binding.btnSecondaryAction.setOnClickListener {
                    navigator.navigateTo(CancelBookingFragment())
                }
            }

            BookingStatus.REFUNDED -> {
                binding.tvCourtTitle.text = "Sân A1 · Cầu lông"
                binding.tvBookingDatetime.text = "Thứ Bảy, 10/10/2026 · 19:00 – 20:00"
                binding.tvBookingCode.text = "DS2026101000005"

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DCFCE7"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BBF7D0"))
                binding.tvBannerIcon.setTextColor(Color.parseColor("#16A34A"))
                binding.tvBannerText.text = "Booking đã hủy hợp lệ theo chính sách. Tiền cọc 75.000đ được hoàn về phương thức thanh toán ban đầu."
                binding.tvBannerText.setTextColor(Color.parseColor("#166534"))

                addPaymentRow("Tổng tiền sân", "250.000đ")
                addPaymentRow("Đã thanh toán cọc (30%)", "75.000đ")
                addPaymentRow("Hoàn cọc", "75.000đ", isSuccess = true)
                addPaymentRow("Hoàn về", "Ví điện tử · 1–3 ngày")

                binding.btnPrimaryAction.text = "Đặt lại sân này"
                binding.btnPrimaryAction.setOnClickListener {
                    val courtId = getCourtIdFromName(binding.tvCourtTitle.text.toString())
                    navigator.navigateTo(BookingFragment.newInstance(courtId))
                }
            }

            BookingStatus.FORFEITED -> {
                binding.tvCourtTitle.text = "Sân B1 · Pickleball"
                binding.tvBookingDatetime.text = "Thứ Năm, 08/10/2026 · 18:00 – 19:00"
                binding.tvBookingCode.text = "DS2026100800014"

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEE2E2"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FECACA"))
                binding.tvBannerIcon.setTextColor(Color.parseColor("#DC2626"))
                binding.tvBannerText.text = "Bạn không đến sau 15 phút kể từ giờ bắt đầu nên booking đã bị hủy và tiền cọc không được hoàn lại."
                binding.tvBannerText.setTextColor(Color.parseColor("#991B1B"))

                addPaymentRow("Tổng tiền sân", "450.000đ")
                addPaymentRow("Đã thanh toán cọc (30%)", "135.000đ")
                addPaymentRow("Mất cọc", "135.000đ", isDanger = true)
                addPaymentRow("Lý do", "Không đến sau 15 phút")

                binding.btnPrimaryAction.text = "Đặt lại sân này"
                binding.btnPrimaryAction.setOnClickListener {
                    val courtId = getCourtIdFromName(binding.tvCourtTitle.text.toString())
                    navigator.navigateTo(BookingFragment.newInstance(courtId))
                }
            }

            BookingStatus.CANCELLED -> {
                binding.tvCourtTitle.text = "Sân A2 · Cầu lông"
                binding.tvBookingDatetime.text = "Thứ Ba, 06/10/2026 · 20:00 – 21:00"
                binding.tvBookingCode.text = "DS2026100600003"

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F4F6"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5E7EB"))
                binding.tvBannerIcon.setTextColor(Color.parseColor("#4B5563"))
                binding.tvBannerText.text = "Booking đã tự động hủy do chưa thanh toán cọc trước hạn. Bạn không bị trừ tiền."
                binding.tvBannerText.setTextColor(Color.parseColor("#374151"))

                addPaymentRow("Tổng tiền sân", "300.000đ")
                addPaymentRow("Cọc cần thanh toán (30%)", "90.000đ")
                addPaymentRow("Trạng thái thanh toán", "Chưa thanh toán")
                addPaymentRow("Lý do hủy", "Quá hạn thanh toán cọc")

                binding.btnPrimaryAction.text = "Đặt lại sân này"
                binding.btnPrimaryAction.setOnClickListener {
                    val courtId = getCourtIdFromName(binding.tvCourtTitle.text.toString())
                    navigator.navigateTo(BookingFragment.newInstance(courtId))
                }
            }

            BookingStatus.COMPLETED -> {
                binding.tvCourtTitle.text = "Sân A2 · Cầu lông"
                binding.tvBookingDatetime.text = "Chủ Nhật, 11/10/2026 · 16:00 – 17:00"
                binding.tvBookingCode.text = "DS2026101100007"

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0E7FF"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C7D2FE"))
                binding.tvBannerIcon.setTextColor(ContextCompat.getColor(ctx, R.color.brand))
                binding.tvBannerText.text = "Cảm ơn bạn đã sử dụng sân. Tiền cọc đã được khấu trừ vào tổng tiền sân."
                binding.tvBannerText.setTextColor(Color.parseColor("#3730A3"))

                addPaymentRow("Tổng tiền sân", "300.000đ")
                addPaymentRow("Cọc đã khấu trừ (30%)", "-90.000đ", isBrand = true)
                addPaymentRow("Đã thanh toán tại sân", "210.000đ", isBold = true)

                binding.btnPrimaryAction.text = "Đặt lại sân này"
                binding.btnPrimaryAction.setOnClickListener {
                    val courtId = getCourtIdFromName(binding.tvCourtTitle.text.toString())
                    navigator.navigateTo(BookingFragment.newInstance(courtId))
                }
            }

            BookingStatus.PASSING -> {
                binding.tvCourtTitle.text = customCourt ?: "Sân A3 · Cầu lông"
                val dateStr = customDate ?: "Thứ Sáu, 16/10/2026"
                val timeStr = customTime ?: "18:00 – 20:00"
                binding.tvBookingDatetime.text = "$dateStr · $timeStr"
                binding.tvBookingCode.text = customCode ?: "DS2026101600031"

                val total = customTotal ?: 400_000
                val deposit = customDeposit ?: 120_000

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0E7FF"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C7D2FE"))
                binding.tvBannerIcon.setTextColor(ContextCompat.getColor(ctx, R.color.brand))
                binding.tvBannerText.text = "Tin pass đang hiển thị cho khách hàng khác. Nếu có người nhận, bạn nhận lại ${deposit.vnd()} tiền cọc."
                binding.tvBannerText.setTextColor(Color.parseColor("#3730A3"))

                addPaymentRow("Tổng tiền sân", total.vnd())
                addPaymentRow("Đã thanh toán cọc (30%)", deposit.vnd())
                addPaymentRow("Tin pass", "Hết hạn 16:00 · 16/10")
                addPaymentRow("Nhận lại khi pass thành công", deposit.vnd(), isBrand = true)
                addPaymentRow("Phí pass", "0đ")

                binding.btnPrimaryAction.text = "Gỡ tin pass"
                binding.btnPrimaryAction.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEE2E2"))
                binding.btnPrimaryAction.setTextColor(Color.parseColor("#DC2626"))
                binding.btnPrimaryAction.setOnClickListener {
                    Toast.makeText(ctx, "Đã gỡ tin pass thành công", Toast.LENGTH_SHORT).show()
                    navigator.navigateAsRoot(BookingHistoryFragment())
                }
            }

            BookingStatus.TRANSFERRED -> {
                binding.tvCourtTitle.text = customCourt ?: "Sân A3 · Cầu lông"
                val dateStr = customDate ?: "Thứ Sáu, 16/10/2026"
                val timeStr = customTime ?: "18:00 – 20:00"
                binding.tvBookingDatetime.text = "$dateStr · $timeStr"
                binding.tvBookingCode.text = customCode ?: "DS2026101600031"

                val total = customTotal ?: 400_000
                val deposit = customDeposit ?: 120_000

                binding.bannerInfo.setBackgroundResource(R.drawable.bg_outline)
                binding.bannerInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DCFCE7"))
                binding.tvBannerIcon.text = "i"
                binding.tvBannerIcon.setBackgroundResource(R.drawable.bg_icon_circle)
                binding.tvBannerIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BBF7D0"))
                binding.tvBannerIcon.setTextColor(Color.parseColor("#16A34A"))
                binding.tvBannerText.text = "Booking đã chuyển cho Lê H. N. lúc 10:32. Bạn đã nhận lại ${deposit.vnd()} tiền cọc."
                binding.tvBannerText.setTextColor(Color.parseColor("#166534"))

                addPaymentRow("Tổng tiền sân", total.vnd())
                addPaymentRow("Tiền cọc nhận lại", deposit.vnd(), isSuccess = true)
                addPaymentRow("Người nhận", "Lê H. N.")
                addPaymentRow("Nhận lại qua", "Ví điện tử · 1–3 ngày")
                addPaymentRow("Quyền sử dụng sân", "Đã chuyển cho người nhận")

                binding.btnPrimaryAction.text = "Về lịch sử đặt sân"
                binding.btnPrimaryAction.setOnClickListener {
                    navigator.navigateAsRoot(BookingHistoryFragment())
                }
            }
        }
    }

    private fun addPaymentRow(label: String, value: String, isBrand: Boolean = false, isBold: Boolean = false, isSuccess: Boolean = false, isDanger: Boolean = false, isWarning: Boolean = false) {
        val ctx = requireContext()
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                if (binding.llPaymentRows.childCount > 0) topMargin = dp(8)
            }
        }

        val tvLabel = TextView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            this.text = label
            textSize = 13f
            setTextColor(ContextCompat.getColor(ctx, R.color.text_muted))
        }

        val tvValue = TextView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            this.text = value
            textSize = 13f
            setTextColor(
                when {
                    isBrand -> ContextCompat.getColor(ctx, R.color.brand)
                    isSuccess -> Color.parseColor("#16A34A")
                    isDanger -> Color.parseColor("#DC2626")
                    isWarning -> Color.parseColor("#D97706")
                    else -> ContextCompat.getColor(ctx, R.color.text_primary)
                }
            )
            if (isBold || isBrand || isSuccess || isDanger) {
                setTypeface(null, Typeface.BOLD)
            }
        }

        row.addView(tvLabel)
        row.addView(tvValue)
        binding.llPaymentRows.addView(row)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_STATUS = "status"
        private const val ARG_COURT = "court"
        private const val ARG_DATE = "date"
        private const val ARG_TIME = "time"
        private const val ARG_TOTAL = "total"
        private const val ARG_DEPOSIT = "deposit"
        private const val ARG_CODE = "code"

        fun newInstance(
            status: BookingStatus,
            court: String? = null,
            date: String? = null,
            time: String? = null,
            total: Int? = null,
            deposit: Int? = null,
            code: String? = null
        ) = BookingDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_STATUS, status.name)
                putString(ARG_COURT, court)
                putString(ARG_DATE, date)
                putString(ARG_TIME, time)
                if (total != null) putInt(ARG_TOTAL, total)
                if (deposit != null) putInt(ARG_DEPOSIT, deposit)
                putString(ARG_CODE, code)
            }
        }
    }
}
