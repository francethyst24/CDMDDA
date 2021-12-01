package com.example.cdmdda.view

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.view.adapter.DiseaseNameDataAdapter
import com.example.cdmdda.databinding.ActivitySearchableBinding
import com.example.cdmdda.model.SuggestionsProvider
import com.example.cdmdda.viewmodel.SearchableViewModel
import java.util.*
import com.example.cdmdda.viewmodel.factory.SearchableViewModelFactory as ViewModelFactory

class SearchableActivity : AppCompatActivity(), DiseaseNameDataAdapter.OnItemClickListener {

    private lateinit var layout: ActivitySearchableBinding
    private lateinit var viewModel: SearchableViewModel

    private lateinit var query: String
    private var dataList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivitySearchableBinding.inflate(layoutInflater)
        setSupportActionBar(layout.toolbarSearch)

        handleIntent(intent)

        setContentView(layout.root)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH != intent.action) return
        query = intent.getStringExtra(SearchManager.QUERY).toString().also { query ->
            // Saving Queries
            SearchRecentSuggestions(this, SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE)
                .saveRecentQuery(query, null)
        }
        // init: ViewModel
        viewModel = ViewModelProvider(this, ViewModelFactory(application, query))
            .get(SearchableViewModel::class.java)
        // update UI : query -> Toolbar(Title)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = viewModel.toolbarTitle
        }
        // capitalize each word
        query = capitalize(query)
        // startSearch on populate: dataList
        viewModel.diseaseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) dataList.add(document.id)
                startStringSearch()
            }
        }
    }

    private fun startStringSearch() {
        val resultList = mutableListOf<String>()
        // search algorithm
        for (data in dataList) {
            val wordCount = data.count { it == " ".single() }
            for (i in 0..wordCount) {
                val cutString = data.split(Regex(" "), i+1).last()
                if (cutString.startsWith(query)) resultList.add(data)
            }
        }
        // update UI if no results
        if (resultList.isEmpty()) {
            layout.loadingSearch.hide()
            layout.textNoResults.visibility = View.VISIBLE
            return
        }
        // submit results(sorted) to RecyclerView
        resultList.sort()
        setSearchRecyclerView(resultList)
    }

    private fun setSearchRecyclerView(resultList: List<String>) {
        val resultsAdapter = DiseaseNameDataAdapter(resultList, query)
        layout.rcvSearchResults.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@SearchableActivity)
            layout.loadingSearch.hide()
            adapter = resultsAdapter
        }
        resultsAdapter.setOnItemClickListener(this@SearchableActivity)
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { this@SearchableActivity.finish(); true }
        else -> { super.onContextItemSelected(item) }
    }

    override fun onResultItemClick(diseaseId: String) {
        val startDiseaseActivity = Intent(this@SearchableActivity, DisplayDiseaseActivity::class.java)
        startDiseaseActivity.putExtra("disease_id", diseaseId)
        startActivity(startDiseaseActivity)
    }

    private fun capitalize(string: String) : String {
        return string.split(Regex("\\s+")).joinToString(" ") {
            it.replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) {
                    firstChar.titlecase(Locale.getDefault())
                } else { firstChar.toString() }
            }
        }
    }

}