package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.databinding.ItemCropBinding
import com.example.cdmdda.databinding.ItemCropBinding.inflate
import com.example.cdmdda.presentation.adapter.CropItemAdapter.CropItemHolder

class CropItemAdapter constructor(
    private val list: List<CropItem>,
    private val onItemClicked: (CropItem) -> Unit
) : RecyclerView.Adapter<CropItemHolder>() {

    inner class CropItemHolder constructor(
        private val itemLayout: ItemCropBinding,
    ) : ViewHolder(itemLayout.root) {

        fun bind(position: Int) = itemLayout.run {
            val uiState = list[position]
            Glide.with(itemView).apply {
                if (position == bindingAdapterPosition) {
                    load(uiState.bannerId).into(imageCropItemBanner)
                } else {
                    clear(imageCropItemBanner)
                    imageCropItemBanner.setImageDrawable(null)
                }
            }
            textCropItem.text = itemView.context.getString(uiState.name)
            iconCropItemSupported.visibility = if (uiState.isDiagnosable) View.VISIBLE else View.GONE
            root.setOnClickListener { onItemClicked(uiState) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropItemHolder {
        val itemLayout = inflate(LayoutInflater.from(parent.context), parent, false)
        return CropItemHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: CropItemHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = list.size

}