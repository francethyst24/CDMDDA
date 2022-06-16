package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cdmdda.data.dto.OnboardingUiState
import com.example.cdmdda.databinding.ItemOnboardingBinding
import com.example.cdmdda.databinding.ItemOnboardingBinding.inflate
import com.example.cdmdda.presentation.adapter.OnboardingAdapter.OnboardingViewHolder

class OnboardingAdapter constructor(
    private val list: List<OnboardingUiState>
) : RecyclerView.Adapter<OnboardingViewHolder>() {

    inner class OnboardingViewHolder constructor(
        private val layout: ItemOnboardingBinding,
    ) : RecyclerView.ViewHolder(layout.root) {

        fun bind(position: Int) = with(layout) {
            val uiState = list[position]
            Glide.with(itemView).apply {
                if (position == bindingAdapterPosition) {
                    load(uiState.imageRes).into(imageOnboarding)
                } else {
                    clear(imageOnboarding)
                    imageOnboarding.setImageDrawable(null)
                }
            }
            textTitleOnboarding.text = uiState.title
            textDescOnboarding.text = uiState.descEn
            textDescTlOnboarding.text = uiState.descTl
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val itemLayout = inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = list.size

}