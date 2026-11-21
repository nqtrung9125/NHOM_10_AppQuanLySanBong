package com.nhom10.appquanlysanbong.ui.weather

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.databinding.FragmentWeatherBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-17 Dự báo thời tiết sân thể thao: thời tiết hiện tại, theo giờ và gợi ý dời lịch. */
class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private data class HourlySlot(
        val time: String,
        val temp: String,
        val rainChance: Int,
        val isAlert: Boolean
    )

    private val slots = listOf(
        HourlySlot("14:00", "30°", 10, false),
        HourlySlot("16:00", "29°", 35, false),
        HourlySlot("17:00", "27°", 80, true),
        HourlySlot("18:00", "26°", 75, true),
        HourlySlot("19:30", "26°", 20, false),
        HourlySlot("21:00", "25°", 10, false)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWeatherBinding.bind(view)

        val navigator = requireActivity() as Navigator
        binding.appbar.tvAppbarTitle.text = "Thời tiết sân thể thao"
        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(binding.bottomNav, NavTab.HOME, navigator)
        renderHourlySlots()

        binding.btnViewReschedule.setOnClickListener {
            navigator.navigateTo(RescheduleFragment())
        }
    }

    private fun renderHourlySlots() {
        binding.llHourlyWeather.removeAllViews()
        val ctx = requireContext()
        val alertRed = Color.parseColor("#DC2626")
        val normalRain = ContextCompat.getColor(ctx, R.color.brand)

        slots.forEachIndexed { index, slot ->
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(dp(12), dp(10), dp(12), dp(10))
                background = ContextCompat.getDrawable(
                    ctx,
                    if (slot.isAlert) R.drawable.bg_outline else R.drawable.bg_card
                )
                layoutParams = LinearLayout.LayoutParams(dp(74), LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    if (index > 0) marginStart = dp(8)
                }
            }

            card.addView(TextView(ctx).apply {
                text = slot.time
                textSize = 12f
                setTextColor(if (slot.isAlert) alertRed else ContextCompat.getColor(ctx, R.color.text_muted))
            })

            card.addView(TextView(ctx).apply {
                text = "☁"
                textSize = 20f
                gravity = Gravity.CENTER
                setPadding(0, dp(4), 0, dp(4))
            })

            card.addView(TextView(ctx).apply {
                text = slot.temp
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(ContextCompat.getColor(ctx, R.color.text_primary))
            })

            card.addView(TextView(ctx).apply {
                text = "${slot.rainChance}%"
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(if (slot.isAlert) alertRed else normalRain)
            })

            binding.llHourlyWeather.addView(card)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}