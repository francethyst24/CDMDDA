package com.example.cdmdda.view.adapter

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropHolder {
        val itemBinding = ItemCropBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CropHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CropHolder, position: Int, model: Crop) {
        holder.bind(model)
    }

    inner class CropHolder(itemBinding: ItemCropBinding) : ViewHolder(itemBinding.root) {
        private val textCropName = itemBinding.textCropName
        init {
            itemBinding.root.setOnClickListener{
                val position = adapterPosition
                if (position != NO_POSITION) {
                    listener.onCropItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }
        fun bind(crop: Crop) { textCropName.text = crop.name }
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