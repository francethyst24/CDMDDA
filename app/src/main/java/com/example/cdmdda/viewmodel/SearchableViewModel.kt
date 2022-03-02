package com.example.cdmdda.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.TextRepository
import com.example.cdmdda.view.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import java.util.*

class SearchableViewModel(application: Application, private val query: String, textRepository: TextRepository) : AndroidViewModel(application) {
    val toolbarTitle = textRepository.getString(R.string.title_search_toolbar).plus(" $query")
    private val diseases = application.resources.getStringArray(R.array.string_diseases).toList()

    fun results() : LiveData<List<String>> = liveData(Dispatchers.IO) {
        val query = StringUtils.capitalize(query)
        val results = mutableListOf<String>()
        // search algorithm
        for (disease in diseases) {
            val wordCount = disease.count { it == " ".single() }
            for (i in 0..wordCount) {
                val word = disease.split(Regex(" "), i+1).last()
                if (word.startsWith(query)) {
                    results.add(disease)
                    results.sort()
                    emit(results)
                }
            }
        }
    }


}