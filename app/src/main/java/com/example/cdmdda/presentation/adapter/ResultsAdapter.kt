package com.example.cdmdda.presentation.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.R
import com.example.cdmdda.data.dto.DiseaseItemUiState
import com.example.cdmdda.databinding.ItemSearchResultsBinding

class ResultsAdapter(
    private val list: List<DiseaseItemUiState>,
    private val query: String,
    private val onItemClick: (DiseaseItemUiState) -> Unit,
) : RecyclerView.Adapter<ResultsAdapter.ResultsHolder>() {

    inner class ResultsHolder(private val itemLayout: ItemSearchResultsBinding) : RecyclerView.ViewHolder(itemLayout.root) {

        fun bind(disease: DiseaseItemUiState) = itemLayout.apply {
            root.setOnClickListener { onItemClick(disease) }
            textSearchName.text = disease.id.highlight(
                ContextCompat.getColor(itemView.context, R.color.ochre_200)
            )
            iconSearchItemSupported.visibility = if (disease.isDetectable) View.VISIBLE else View.GONE
        }

        private fun String.highlight(color: Int): SpannableString {
            return SpannableString(this).also {
                val startIndex = indexOf(query)
                it.setSpan(
                    BackgroundColorSpan(color),
                    startIndex,
                    (startIndex + query.length),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsHolder {
        val itemBinding = ItemSearchResultsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultsHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ResultsHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

}