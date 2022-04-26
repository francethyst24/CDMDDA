package com.example.cdmdda.presentation.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.R
import com.example.cdmdda.common.utils.AndroidUtils.getColorCompat
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.databinding.ItemSearchResultsBinding
import com.example.cdmdda.databinding.ItemSearchResultsBinding.inflate
import com.example.cdmdda.presentation.adapter.SearchQueryAdapter.ResultsHolder

class SearchQueryAdapter(
    private val list: List<DiseaseItem>,
    private val query: String,
    private val onItemClick: (DiseaseItem) -> Unit,
) : RecyclerView.Adapter<ResultsHolder>() {

    inner class ResultsHolder(private val itemLayout: ItemSearchResultsBinding) : ViewHolder(itemLayout.root) {

        fun bind(disease: DiseaseItem) = with(itemLayout) {
            val color = itemView.context.getColorCompat(R.color.ochre_200)
            textSearchName.text = disease.id.highlight(color)
            root.setOnClickListener { onItemClick(disease) }
            iconSearchItemSupported.visibility = if (disease.isDetectable) View.VISIBLE else View.GONE
        }

        private fun String.highlight(color: Int): SpannableString {
            return SpannableString(this@highlight).also {
                val startIndex = indexOf(query)
                val bgColor = BackgroundColorSpan(color)
                it.setSpan(
                    bgColor,
                    startIndex,
                    (startIndex + query.length),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsHolder {
        val itemLayout = inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultsHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ResultsHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

}