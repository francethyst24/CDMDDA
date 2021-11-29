package com.example.cdmdda.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.databinding.ItemDiseaseBinding
import com.example.cdmdda.dto.Disease
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class SearchAdapter(options: FirestoreRecyclerOptions<Disease>)
    : FirestoreRecyclerAdapter<Disease, SearchAdapter.DiseaseHolder>(options) {
    lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseHolder {
        val itemBinding = ItemDiseaseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DiseaseHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DiseaseHolder, position: Int, model: Disease) {
        holder.bind(model)
    }

    inner class DiseaseHolder(itemBinding: ItemDiseaseBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val textDiseaseName = itemBinding.textSearchName
        init {
            itemBinding.root.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDiseaseItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }
        fun bind(disease: Disease) { textDiseaseName.text = disease.name }
    }

    interface OnItemClickListener {
        fun onDiseaseItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) { this.listener = listener }

}