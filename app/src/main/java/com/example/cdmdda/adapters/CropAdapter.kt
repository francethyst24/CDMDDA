package com.example.cdmdda.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.cdmdda.databinding.CropItemBinding
import com.example.cdmdda.dto.Crop
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class CropAdapter(options: FirestoreRecyclerOptions<Crop>) :
    FirestoreRecyclerAdapter<Crop, CropAdapter.CropHolder>(options)
{
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropHolder {
        val itemBinding = CropItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CropHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CropHolder, position: Int, model: Crop) {
        holder.bind(model)
    }

    inner class CropHolder(itemBinding: CropItemBinding) : ViewHolder(itemBinding.root) {
        var textCropName : TextView = itemBinding.textCropName
        init {
            itemBinding.root.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }
        fun bind(crop: Crop) { textCropName.text = crop.name }
    }

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) { this.listener = listener }
}