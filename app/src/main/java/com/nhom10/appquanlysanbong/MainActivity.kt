package com.nhom10.appquanlysanbong

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nhom10.appquanlysanbong.databinding.ActivityMainBinding
import com.nhom10.appquanlysanbong.ui.splash.SplashFragment

/** Host các Fragment. Việc chọn màn gốc theo phiên nằm ở ActorNavigation. */
class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            binding.statusBarSpacer.layoutParams.height = bars.top
            binding.statusBarSpacer.requestLayout()
            v.setPadding(bars.left, 0, bars.right, maxOf(bars.bottom, ime.bottom))
            insets
        }

        if (savedInstanceState == null) {
            navigateTo(SplashFragment(), addToBackStack = false)
        }
    }

    override fun navigateTo(fragment: Fragment, addToBackStack: Boolean) {
        val tx = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) tx.addToBackStack(null)
        tx.commit()
    }

    override fun navigateAsRoot(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun setStatusBarLight(light: Boolean) {
        binding.statusBarSpacer.setBackgroundColor(
            ContextCompat.getColor(this, if (light) R.color.background else R.color.brand_dark)
        )
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = light
    }
}
