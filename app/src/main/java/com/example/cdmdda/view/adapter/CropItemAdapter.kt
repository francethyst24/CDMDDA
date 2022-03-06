package com.example.cdmdda.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.databinding.ItemCropBinding
import com.example.cdmdda.model.dto.CropItem

class CropItemAdapter(private val list: List<CropItem>, private val dataset: String) : RecyclerView.Adapter<CropItemAdapter.CropItemHolder>() {
    private lateinit var listener: CropItemEventListener

    inner class CropItemHolder(private val itemLayout: ItemCropBinding) : RecyclerView.ViewHolder(itemLayout.root) {
        init {
            itemLayout.root.setOnClickListener{
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCropItemClick(list[position].cropId)
                }
            }
        }

        fun bind(crop: CropItem) {
            itemLayout.textCropItem.text = crop.name
            itemLayout.imageCropItemBanner.setImageDrawable(crop.banner)
            if (crop.isSupported) itemLayout.iconCropItemSupported.visibility = View.VISIBLE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropItemHolder {
        val itemBinding = ItemCropBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CropItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CropItemHolder, position: Int) {
        holder.bind(list[position])
    }

    interface CropItemEventListener {
        fun onCropItemClick(cropId: String)
    }

    fun setOnItemClickListener(_listener: CropItemEventListener) {
        listener = _listener
    }

    override fun getItemCount(): Int = list.size

}