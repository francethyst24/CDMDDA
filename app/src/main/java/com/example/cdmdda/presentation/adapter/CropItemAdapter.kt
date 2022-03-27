package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cdmdda.data.dto.CropItemUiState
import com.example.cdmdda.databinding.ItemCropBinding

class CropItemAdapter(
    private val list: List<CropItemUiState>,
    private val onItemClicked: (CropItemUiState) -> Unit
) : RecyclerView.Adapter<CropItemAdapter.CropItemHolder>() {

    inner class CropItemHolder(private val itemLayout: ItemCropBinding) : RecyclerView.ViewHolder(itemLayout.root) {

        fun bind(cropItem: CropItemUiState) = itemLayout.apply {
            Glide.with(itemView).load(cropItem.bannerId).into(imageCropItemBanner)
            textCropItem.text = cropItem.name
            iconCropItemSupported.visibility = if (cropItem.isSupported) View.VISIBLE else View.GONE
            root.setOnClickListener {
                onItemClicked(cropItem)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropItemHolder {
        val itemBinding = ItemCropBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CropItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CropItemHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}