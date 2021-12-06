package com.example.cdmdda.view.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ItemSearchResultsBinding

class DiseaseNameDataAdapter(private val list: List<String>, private val query: String) : RecyclerView.Adapter<DiseaseNameDataAdapter.ResultsHolder>() {
    lateinit var listener: OnItemClickListener

    inner class ResultsHolder(private val itemBinding: ItemSearchResultsBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemBinding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onResultItemClick(list[position])
                }
            }
        }

        fun bind(context: Context, position: Int) {
            val spannableString = SpannableString(list[position])
            val color = ContextCompat.getColor(context, R.color.design_default_color_secondary)
            val startIndex = list[position].indexOf(query)
            spannableString.setSpan(BackgroundColorSpan(color), startIndex,
                (startIndex + query.length), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            itemBinding.textSearchName.text = spannableString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsHolder {
        val itemBinding = ItemSearchResultsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultsHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ResultsHolder, position: Int) = holder.bind(holder.itemView.context, position)

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onResultItemClick(diseaseId: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}