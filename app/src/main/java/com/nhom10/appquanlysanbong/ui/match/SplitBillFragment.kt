package com.nhom10.appquanlysanbong.ui.match

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentSplitBillBinding
import com.nhom10.appquanlysanbong.ui.common.vnd

/** KH-13 Chia tiền nhóm: theo dõi số tiền đã thu, nhắc người chưa trả và chia sẻ link. */
class SplitBillFragment : Fragment(R.layout.fragment_split_bill) {

    private var _binding: FragmentSplitBillBinding? = null
    private val binding get() = _binding!!

    private data class UnpaidMember(
        val name: String,
        val initial: String,
        val avatarColorHex: String
    )

    private val unpaidMembers = listOf(
        UnpaidMember("Long", "L", "#F43F5E"),
        UnpaidMember("Phát", "P", "#6366F1"),
        UnpaidMember("Đạt", "Đ", "#4F46E5")
    )

    private val paidInitials = listOf(
        "T" to "#F97316",
        "V" to "#6366F1",
        "V" to "#4F46E5",
        "K" to "#D97706",
        "N" to "#374151"
    )

    private val totalCourtVnd = 500_000
    private val perShareVnd = 50_000
    private val totalMembers = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplitBillBinding.bind(view)

        binding.appbar.tvAppbarTitle.text = "Chia tiền nhóm"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        renderOverview()
        renderUnpaidList()
        renderPaidAvatars()

        binding.btnShareLink.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Chia tiền sân C1 (15/10 · 17:00 – 19:00) — Mỗi người ${perShareVnd.vnd()}: https://vaosan.app/pay/DS2026101500012"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ link thanh toán"))
        }
    }

    private fun renderOverview() {
        val paidCount = totalMembers - unpaidMembers.size
        val collectedVnd = paidCount * perShareVnd
        val progress = (collectedVnd * 100) / totalCourtVnd

        binding.tvCollectedAmount.text = collectedVnd.vnd()
        binding.tvTargetAmount.text = "/ ${totalCourtVnd.vnd()} đã thu"
        binding.pbSplitProgress.progress = progress
        binding.tvSplitSummary.text = "$paidCount/$totalMembers người đã trả · ${perShareVnd.vnd()} mỗi người"
        binding.tvUnpaidHeader.text = "Chưa trả (${unpaidMembers.size})"
        binding.tvPaidHeader.text = "Đã trả ($paidCount)"
    }

    private fun renderUnpaidList() {
        binding.llUnpaidList.removeAllViews()
        val ctx = requireContext()
        val brand = ContextCompat.getColor(ctx, R.color.brand)

        unpaidMembers.forEachIndexed { index, member ->
            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(10), 0, dp(10))
            }

            val avatar = TextView(ctx).apply {
                text = member.initial
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                background = ContextCompat.getDrawable(ctx, R.drawable.bg_icon_circle)
                backgroundTintList = ColorStateList.valueOf(Color.parseColor(member.avatarColorHex))
                layoutParams = LinearLayout.LayoutParams(dp(38), dp(38))
            }
            row.addView(avatar)

            val infoBox = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, dp(8), 0)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            infoBox.addView(TextView(ctx).apply {
                text = member.name
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(ContextCompat.getColor(ctx, R.color.text_primary))
            })
            infoBox.addView(TextView(ctx).apply {
                text = "Chưa thanh toán"
                textSize = 12f
                setTextColor(ContextCompat.getColor(ctx, R.color.warning_text))
            })
            row.addView(infoBox)

            val remindBtn = TextView(ctx).apply {
                text = "Nhắc"
                textSize = 13f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(brand)
                setPadding(dp(18), dp(8), dp(18), dp(8))
                background = ContextCompat.getDrawable(ctx, R.drawable.bg_outline)
                setOnClickListener {
                    Toast.makeText(ctx, "Đã gửi lời nhắc tới ${member.name}", Toast.LENGTH_SHORT).show()
                }
            }
            row.addView(remindBtn)

            binding.llUnpaidList.addView(row)

            if (index < unpaidMembers.lastIndex) {
                binding.llUnpaidList.addView(View(ctx).apply {
                    setBackgroundColor(ContextCompat.getColor(ctx, R.color.border_card))
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1))
                })
            }
        }
    }

    private fun renderPaidAvatars() {
        binding.llPaidAvatars.removeAllViews()
        val ctx = requireContext()
        paidInitials.forEachIndexed { index, (letter, colorHex) ->
            val bubble = TextView(ctx).apply {
                text = letter
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                background = ContextCompat.getDrawable(ctx, R.drawable.bg_icon_circle)
                backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))
                layoutParams = LinearLayout.LayoutParams(dp(30), dp(30)).apply {
                    if (index > 0) marginStart = -dp(6)
                }
            }
            binding.llPaidAvatars.addView(bubble)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}