package com.example.cdmdda.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.TextRepository
import com.example.cdmdda.view.utils.capitalize
import kotlinx.coroutines.Dispatchers

class SearchableViewModel(application: Application, private val query: String, textRepository: TextRepository) : AndroidViewModel(application) {
    val toolbarTitle = textRepository.getString(R.string.ui_head_search).plus(": $query")
    private val diseases = application.resources.getStringArray(R.array.string_diseases).toList()

    fun results() : LiveData<List<String>> = liveData(Dispatchers.IO) {
        val query = query.capitalize()
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