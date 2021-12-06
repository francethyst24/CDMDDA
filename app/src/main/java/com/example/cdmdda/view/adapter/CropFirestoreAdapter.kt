package com.example.cdmdda.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import com.example.cdmdda.databinding.ItemCropBinding
import com.example.cdmdda.model.dto.Crop
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class CropFirestoreAdapter(options: FirestoreRecyclerOptions<Crop>) : FirestoreRecyclerAdapter<Crop, CropFirestoreAdapter.CropHolder>(options) {
    private lateinit var listener: CropEventListener

    inner class CropHolder(private val itemBinding: ItemCropBinding) : ViewHolder(itemBinding.root) {
        init {
            itemBinding.root.setOnClickListener{
                val position = bindingAdapterPosition
                if (position != NO_POSITION) {
                    listener.onCropItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }

        fun bind(context: Context, crop: Crop) {
            itemBinding.textCropName.text = crop.getName(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropHolder {
        val itemBinding = ItemCropBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CropHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CropHolder, position: Int, model: Crop) {
        holder.bind(holder.itemView.context, model)
    }

    interface CropEventListener {
        fun onCropItemClick(documentSnapshot: DocumentSnapshot, position: Int)
        fun onCropQueryReturned(itemCount: Int)
    }

    fun setOnItemClickListener(_listener: CropEventListener) {
        listener = _listener
    }

    override fun onDataChanged() {
        super.onDataChanged()
        listener.onCropQueryReturned(itemCount)
    }

}