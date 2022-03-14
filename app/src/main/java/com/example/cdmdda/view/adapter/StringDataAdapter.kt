package com.example.cdmdda.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.databinding.ItemStringBinding

class StringDataAdapter(private val list: List<String>) : RecyclerView.Adapter<StringDataAdapter.SymptomHolder>() {

    inner class SymptomHolder(private val itemBinding: ItemStringBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(position: Int) {
            itemBinding.textItemSymptom.text = list[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomHolder {
        val itemBinding = ItemStringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SymptomHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SymptomHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = list.size

}