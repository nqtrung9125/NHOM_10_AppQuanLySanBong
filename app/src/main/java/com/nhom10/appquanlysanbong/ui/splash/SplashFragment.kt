package com.nhom10.appquanlysanbong.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.nhom10.appquanlysanbong.R
import com.nhom10.appquanlysanbong.ui.auth.openActorHome

/** Mở trang chủ phù hợp với phiên đã lưu. */
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val handler = Handler(Looper.getMainLooper())
    private val goHome = Runnable {
        if (isAdded) {
            openActorHome()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.postDelayed(goHome, 1200)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(goHome)
        super.onDestroyView()
    }
}
