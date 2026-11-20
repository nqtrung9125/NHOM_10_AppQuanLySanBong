
package com.nhom10.appquanlysanbong.ui.notifications

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.Navigator
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.data.AppNotification
import com.nhom10.appquanlysanbong.data.DemoData
import com.nhom10.appquanlysanbong.databinding.FragmentNotificationsBinding
import com.nhom10.appquanlysanbong.ui.common.BottomNav
import com.nhom10.appquanlysanbong.ui.common.NavTab

/** KH-10: Danh sách thông báo. */
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val notices = DemoData.notifications

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNotificationsBinding.bind(view)

        binding.appbar.tvAppbarTitle.text = "Thông báo"

        binding.appbar.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        BottomNav.setup(
            binding.bottomNav,
            NavTab.HOME,
            requireActivity() as Navigator
        )

        binding.llNotifications.removeAllViews()

        notices.forEach { notice ->
            binding.llNotifications.addView(createNoticeCard(notice))
        }
    }

    private fun createNoticeCard(notice: AppNotification): View {
        val ctx = requireContext()

        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = ContextCompat.getDrawable(ctx, R.drawable.bg_card)
            elevation = dp(1).toFloat()
        }

        val iconView = TextView(ctx).apply {
            text = notice.icon
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(ctx, R.color.brand))
            background = ContextCompat.getDrawable(ctx, R.drawable.bg_icon_circle)
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
        }

        card.addView(iconView)

        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val titleRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = TextView(ctx).apply {
            text = notice.title
            textSize = 14f
            setTextColor(ContextCompat.getColor(ctx, R.color.text_primary))
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        titleRow.addView(title)

        if (notice.isUnread) {
            titleRow.addView(TextView(ctx).apply {
                text = "●"
                textSize = 10f
                setTextColor(ContextCompat.getColor(ctx, R.color.brand))
                contentDescription = "Thông báo chưa đọc"
            })
        }

        content.addView(titleRow)

        content.addView(TextView(ctx).apply {
            text = notice.message
            textSize = 13f
            setTextColor(ContextCompat.getColor(ctx, R.color.text_muted))
            setPadding(0, dp(5), 0, dp(5))
        })

        content.addView(TextView(ctx).apply {
            text = notice.time
            textSize = 12f
            setTextColor(ContextCompat.getColor(ctx, R.color.text_hint))
        })

        card.addView(content)

        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(10)
        }

        card.setOnClickListener {
            Toast.makeText(
                ctx,
                "Đã mở thông báo: ${notice.title}",
                Toast.LENGTH_SHORT
            ).show()
        }

        return card
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
