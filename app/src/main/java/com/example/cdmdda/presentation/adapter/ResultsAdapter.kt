package com.example.cdmdda.presentation.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.R
import com.example.cdmdda.common.ContextUtils.getColorCompat
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.databinding.ItemSearchResultsBinding
import com.example.cdmdda.databinding.ItemSearchResultsBinding.inflate
import com.example.cdmdda.presentation.adapter.ResultsAdapter.ResultsHolder
import kotlinx.coroutines.*

class ResultsAdapter constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val list: List<DiseaseItem>,
    private val query: String,
    private val onItemClick: (DiseaseItem) -> Unit,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : RecyclerView.Adapter<ResultsHolder>() {

    inner class ResultsHolder(private val itemLayout: ItemSearchResultsBinding) : ViewHolder(itemLayout.root) {

        fun bind(disease: DiseaseItem) = itemLayout.run {
            lifecycleOwner.lifecycleScope.launch(defaultDispatcher) {
                val color = withContext(ioDispatcher) {
                    itemView.context.getColorCompat(R.color.ochre_200)
                }
                textSearchName.text = disease.id.highlight(color)
            }
            root.setOnClickListener { onItemClick(disease) }
            iconSearchItemSupported.visibility = if (disease.isDetectable) View.VISIBLE else View.GONE
        }

        private suspend fun String.highlight(color: Int) = withContext(defaultDispatcher) {
            return@withContext SpannableString(this@highlight).also {
                val startIndexAsync = async { indexOf(query) }
                val bgColorAsync = async(ioDispatcher) { BackgroundColorSpan(color) }
                val startIndex = startIndexAsync.await()
                it.setSpan(
                    bgColorAsync.await(),
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