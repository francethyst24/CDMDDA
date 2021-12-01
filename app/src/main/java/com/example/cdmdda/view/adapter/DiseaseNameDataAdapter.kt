package com.example.cdmdda.view.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ItemSearchResultsBinding

class DiseaseNameDataAdapter(list: List<String>, query: String) : RecyclerView.Adapter<DiseaseNameDataAdapter.ResultsHolder>() {
    lateinit var listener: OnItemClickListener
    private val itemCount = list.size
    private val _list = list.toMutableList() as List<String>
    private val _query = query

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseNameDataAdapter.ResultsHolder {
        val itemBinding = ItemSearchResultsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultsHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DiseaseNameDataAdapter.ResultsHolder, position: Int) {
        val spannableString = SpannableString(_list[position])
        val color = ContextCompat.getColor(holder.textResultName.context, R.color.design_default_color_secondary)
        val startIndex = _list[position].indexOf(_query)
        spannableString.setSpan(BackgroundColorSpan(color), startIndex,
            (startIndex + _query.length), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.textResultName.text = spannableString
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    inner class ResultsHolder(itemBinding: ItemSearchResultsBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val textResultName = itemBinding.textSearchName
        init {
            itemBinding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onResultItemClick(_list[position])
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onResultItemClick(diseaseId: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) { this.listener = listener }

}