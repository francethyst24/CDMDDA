package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cdmdda.data.dto.ImageResource
import com.example.cdmdda.databinding.ItemImageBinding

class ImageAdapter(private val list: List<ImageResource>) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
    inner class ImageHolder(private val itemBinding: ItemImageBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(position: Int) {
            when (list[position]) {
                is ImageResource.Integer -> {
                    val int = list[position] as ImageResource.Integer
                    Glide.with(itemView).load(int.value).into(itemBinding.imageContent)
                }
                is ImageResource.Uri -> {
                    val uri = list[position] as ImageResource.Uri
                    Glide.with(itemView).load(uri.value).into(itemBinding.imageContent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemBinding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = list.size

}