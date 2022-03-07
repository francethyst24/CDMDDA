package com.example.cdmdda.viewmodel

import android.app.Application
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.model.dto.DiseaseItem
import com.example.cdmdda.view.utils.capitalize
import kotlinx.coroutines.Dispatchers

class SearchableViewModel(application: Application, query: String, dataRepository: DataRepository) : AndroidViewModel(application) {
    val toolbarTitle = dataRepository.getString(R.string.ui_head_search).plus(": $query")
    private val diseases = application.resources.getStringArray(R.array.string_diseases).toList()
    private val query = query.capitalize()

    fun results(context: Context) = liveData(Dispatchers.IO) {
        val dataRepository = DataRepository(context)
        val results = mutableListOf<DiseaseItem>()
        // search algorithm
        for (id in diseases) {
            val wordCount = id.count { it == " ".single() }
            for (i in 0..wordCount) {
                val word = id.split(Regex(" "), i+1).last()
                if (word.startsWith(query)) {
                    results.add(DiseaseItem(id,
                        dataRepository.fetchDiseaseSupported(id),
                        id.highlight(context)
                    ))
                    results.sortBy { it.name }
                    emit(results.toList())
                }
            }
        }
    }

    private fun String.highlight(context: Context) : SpannableString {
        return SpannableString(this).also {
            val color = ContextCompat.getColor(context, R.color.ochre_200)
            val startIndex = this.indexOf(query)
            it.setSpan(
                BackgroundColorSpan(color),
                startIndex,
                (startIndex + query.length),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}