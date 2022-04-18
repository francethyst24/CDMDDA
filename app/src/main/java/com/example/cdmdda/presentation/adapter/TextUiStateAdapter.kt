package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.data.dto.TextUiState
import com.example.cdmdda.databinding.ItemStringBinding
import com.example.cdmdda.databinding.ItemStringBinding.inflate
import com.example.cdmdda.presentation.adapter.TextUiStateAdapter.TextUiStateHolder
import com.example.cdmdda.presentation.adapter.TextViewLinksAdapter.Companion.setAdapter

class TextUiStateAdapter constructor(
    private val list: List<TextUiState>,
    private val onItemClick: (TextUiState) -> Unit,
) : RecyclerView.Adapter<TextUiStateHolder>() {

    inner class TextUiStateHolder(private val itemLayout: ItemStringBinding) : ViewHolder(itemLayout.root) {
        fun bind(item: TextUiState) = itemLayout.run {
            textItemSymptom.text = item.displayName(itemView.context)
            val adapter = TextViewLinksAdapter(listOf(item)) { onItemClick(item) }
            textItemSymptom.setAdapter(adapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextUiStateHolder {
        val itemLayout = inflate(LayoutInflater.from(parent.context), parent, false)
        return TextUiStateHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: TextUiStateHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

}