package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.common.StringArray
import com.example.cdmdda.common.StringList
import com.example.cdmdda.databinding.ItemStringBinding
import com.example.cdmdda.databinding.ItemStringBinding.inflate
import com.example.cdmdda.presentation.adapter.StringAdapter.StringHolder

class StringAdapter constructor(
    private val list: StringArray,
) : RecyclerView.Adapter<StringHolder>() {

    inner class StringHolder(private val itemBinding: ItemStringBinding) : ViewHolder(itemBinding.root) {
        fun bind(position: Int) {
            itemBinding.textItemSymptom.text = list[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringHolder {
        val itemLayout = inflate(LayoutInflater.from(parent.context), parent, false)
        return StringHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: StringHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = list.size

}