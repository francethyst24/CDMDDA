package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.cdmdda.data.dto.ImageResource
import com.example.cdmdda.databinding.ItemImageBinding
import com.example.cdmdda.databinding.ItemImageBinding.inflate
import com.example.cdmdda.presentation.adapter.ImageAdapter.ImageHolder

class ImageAdapter constructor(
    private val list: List<ImageResource>,
) : RecyclerView.Adapter<ImageHolder>() {
    inner class ImageHolder(private val itemBinding: ItemImageBinding) : ViewHolder(itemBinding.root) {
        fun bind(position: Int): Unit = list[position].let {
            Glide.with(itemView).load(
                when (it) {
                    is ImageResource.Res -> it.resId
                    is ImageResource.Uri -> it.value
                }
            ).into(itemBinding.imageContent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemBinding = inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = list.size

}