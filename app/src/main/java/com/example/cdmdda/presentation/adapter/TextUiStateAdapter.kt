package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.DiseaseTextUiState
import com.example.cdmdda.data.dto.TextUiState
import com.example.cdmdda.databinding.ItemStringBinding

class TextUiStateAdapter(
    private val list: List<TextUiState>,
    private val onItemClick: (TextUiState) -> Unit,
) : RecyclerView.Adapter<TextUiStateAdapter.TextUiStateHolder>() {

    inner class TextUiStateHolder(private val itemLayout: ItemStringBinding) : RecyclerView.ViewHolder(itemLayout.root) {

        fun bind(item: TextUiState) = itemLayout.apply {
            textItemSymptom.text = when (item) {
                is CropTextUiState -> item.name
                is DiseaseTextUiState -> item.id
            }

            val adapter = TextViewLinksAdapter(listOf(item)) { onItemClick(item) }
            textItemSymptom.setAdapter(adapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextUiStateHolder {
        val itemLayout = ItemStringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TextUiStateHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: TextUiStateHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}