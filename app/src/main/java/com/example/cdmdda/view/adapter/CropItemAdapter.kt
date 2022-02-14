package com.example.cdmdda.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.databinding.ItemCropBinding
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.TextRepository
import com.example.cdmdda.view.utils.ResourceUtils
import com.example.cdmdda.view.utils.StringUtils

class CropItemAdapter(private val list: List<String>, private val dataset: String) : RecyclerView.Adapter<CropItemAdapter.CropItemHolder>() {
    private lateinit var listener: CropItemEventListener

    inner class CropItemHolder(private val itemLayout: ItemCropBinding) : RecyclerView.ViewHolder(itemLayout.root) {
        init {
            itemLayout.root.setOnClickListener{
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCropItemClick(list[position])
                }
            }
        }

        fun bind(context: Context, cropId: String) {
            val textRepository = TextRepository(context, dataset)
            val imageRepository = ImageRepository(context, dataset)
            itemLayout.textCropItem.text = textRepository.fetchCropName(cropId)
            itemLayout.imageCropItem.setImageDrawable(imageRepository.fetchCropIcon(cropId))
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropItemHolder {
        val itemBinding = ItemCropBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CropItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CropItemHolder, position: Int) {
        holder.bind(holder.itemView.context, StringUtils.toResourceId(list[position]))
    }

    interface CropItemEventListener {
        fun onCropItemClick(cropId: String)
    }

    fun setOnItemClickListener(_listener: CropItemEventListener) {
        listener = _listener
    }

    override fun getItemCount(): Int = list.size

}