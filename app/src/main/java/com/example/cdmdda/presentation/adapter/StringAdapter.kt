package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.databinding.ItemStringBinding

class StringAdapter(private val list: List<String>) : RecyclerView.Adapter<StringAdapter.StringHolder>() {

    inner class StringHolder(private val itemBinding: ItemStringBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(position: Int) {
            itemBinding.textItemSymptom.text = list[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringHolder {
        val itemBinding = ItemStringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StringHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: StringHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = list.size

}