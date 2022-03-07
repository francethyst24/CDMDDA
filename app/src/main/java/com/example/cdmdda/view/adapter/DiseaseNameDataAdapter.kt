package com.example.cdmdda.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.databinding.ItemSearchResultsBinding
import com.example.cdmdda.model.dto.DiseaseItem

class DiseaseNameDataAdapter(private val list: List<DiseaseItem>, private val query: String) : RecyclerView.Adapter<DiseaseNameDataAdapter.ResultsHolder>() {
    private lateinit var listener: OnItemClickListener

    inner class ResultsHolder(private val itemLayout: ItemSearchResultsBinding) : RecyclerView.ViewHolder(itemLayout.root) {
        init {
            itemLayout.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onResultItemClick(list[position].name)
                }
            }
        }

        fun bind(disease: DiseaseItem) {
            itemLayout.textSearchName.text = disease.highlightedName
            itemLayout.iconSearchItemSupported.visibility = if (disease.isSupported) View.VISIBLE else View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsHolder {
        val itemBinding = ItemSearchResultsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultsHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ResultsHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onResultItemClick(diseaseId: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}