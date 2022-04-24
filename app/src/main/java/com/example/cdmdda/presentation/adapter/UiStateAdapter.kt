package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.data.dto.UiState
import com.example.cdmdda.databinding.ItemStringBinding
import com.example.cdmdda.databinding.ItemStringBinding.inflate
import com.example.cdmdda.presentation.adapter.LinkAdapter.Companion.setLinkAdapter
import com.example.cdmdda.presentation.adapter.UiStateAdapter.TextUiStateHolder

class UiStateAdapter constructor(
    private val list: List<UiState>,
    private val onItemClick: (UiState) -> Unit,
) : RecyclerView.Adapter<TextUiStateHolder>() {

    inner class TextUiStateHolder(private val itemLayout: ItemStringBinding) : ViewHolder(itemLayout.root) {
        fun bind(item: UiState) = itemLayout.run {
            textItemSymptom.text = item.displayName(itemView.context)
            val adapter = LinkAdapter(listOf(item)) { onItemClick(item) }
            textItemSymptom.setLinkAdapter(adapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextUiStateHolder {
        val itemLayout = inflate(LayoutInflater.from(parent.context), parent, false)
        return TextUiStateHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: TextUiStateHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

}