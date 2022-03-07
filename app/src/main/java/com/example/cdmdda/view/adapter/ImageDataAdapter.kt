package com.example.cdmdda.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cdmdda.databinding.ItemImageBinding

class ImageDataAdapter(private val list: List<Bitmap>) : RecyclerView.Adapter<ImageDataAdapter.ImageHolder>() {
    inner class ImageHolder(private val itemBinding: ItemImageBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(position: Int) {
            Glide.with(itemView).load(list[position]).into(itemBinding.imageContent)
            // itemBinding.imageContent.setImageBitmap(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemBinding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = list.size

}