package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.cdmdda.R
import com.example.cdmdda.common.utils.AndroidUtils.intent
import com.example.cdmdda.data.repository.OnboardingRepository
import com.example.cdmdda.databinding.ActivityOnboardingBinding
import com.example.cdmdda.presentation.adapter.OnboardingAdapter
import com.example.cdmdda.presentation.viewmodel.OnboardingViewModel
import com.example.cdmdda.presentation.viewmodel.factory.viewModelBuilder

class OnboardingActivity : AppCompatActivity() {
    private lateinit var onboardingAdapter: OnboardingAdapter
    private val layout by lazy {
        ActivityOnboardingBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModelBuilder {
        OnboardingViewModel(OnboardingRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.root)

        viewModel.allOnboardings().observe(this) {
            onboardingAdapter = OnboardingAdapter(it)
            layout.pagerOnboarding.adapter = onboardingAdapter
            setOnboardingIndicators(it.size)
            setCurrentIndicator(0)
        }
        layout.pagerOnboarding.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            }
        )

        layout.buttonProceed.setOnClickListener {
            with(layout) {
                val currentItemInc = pagerOnboarding.currentItem + 1
                if (currentItemInc < onboardingAdapter.itemCount) {
                    pagerOnboarding.setCurrentItem(currentItemInc)
                } else {
                    viewModel.updateSharedPref(applicationContext)
                    startActivity(intent(MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setOnboardingIndicators(size: Int) {
        val indicators = Array(size) { ImageView(this) }
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        layoutParams.setMargins(8, 0, 8, 0)
        indicators.forEach {
            val icRes = R.drawable.ic_onboarding_inactive
            it.setImageDrawable(ContextCompat.getDrawable(this, icRes))
            it.layoutParams = layoutParams
            layout.divOnboardingIndicators.addView(it)
        }
    }

    private fun setCurrentIndicator(index: Int) = with(layout) {
        val childCount = divOnboardingIndicators.childCount
        (0 until childCount).forEach {
            val icRes = if (it == index)
                R.drawable.ic_onboarding_active
            else
                R.drawable.ic_onboarding_inactive
            val imageView = divOnboardingIndicators.getChildAt(it) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(this@OnboardingActivity, icRes)
            )
        }
        buttonProceed.text = if (index + 1 == onboardingAdapter.itemCount)
            getString(R.string.fui_start_default)
        else
            getString(R.string.fui_next_default)
    }

}