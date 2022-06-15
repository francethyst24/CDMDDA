package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.preference.PreferenceManager
import com.example.cdmdda.R
import com.example.cdmdda.common.Constants.ON_INITIAL
import com.example.cdmdda.data.repository.OnboardingRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OnboardingViewModel constructor(
    private val repository: OnboardingRepository,
) : ViewModel() {

    fun allOnboardings() = liveData {
        val list = repository.getAllOnboarding(
            R.array.images_onboarding,
            R.array.titles_onboarding,
            R.array.desc_en_onboarding,
            R.array.desc_tl_onboarding,
        )
        emit(list)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateSharedPref(context: Context) = GlobalScope.launch {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPref.edit().putBoolean(ON_INITIAL, true).apply()
    }

}