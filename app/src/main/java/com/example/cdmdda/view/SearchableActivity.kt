package com.example.cdmdda.view

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.databinding.ActivitySearchableBinding
import com.example.cdmdda.model.SuggestionsProvider
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.model.dto.DiseaseItem
import com.example.cdmdda.view.adapter.DiseaseNameDataAdapter
import com.example.cdmdda.view.utils.capitalize
import com.example.cdmdda.view.utils.interactivityIntent
import com.example.cdmdda.viewmodel.SearchableViewModel
import com.example.cdmdda.viewmodel.factory.SearchableViewModelFactory as ViewModelFactory

class SearchableActivity : BaseCompatActivity(), DiseaseNameDataAdapter.OnItemClickListener {

    private lateinit var layout: ActivitySearchableBinding
    private lateinit var viewModel: SearchableViewModel

    private lateinit var query: String

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
        viewModel = ViewModelProvider(this,
            ViewModelFactory(application, query, DataRepository(this@SearchableActivity))
        ).get(SearchableViewModel::class.java)

        // update UI : query -> Toolbar(Title)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = viewModel.toolbarTitle
        }
        viewModel.results(this).observe(this@SearchableActivity) {
            if (it.isEmpty()) {
                layout.loadingSearch.hide()
                layout.textNoResults.visibility = View.VISIBLE
                return@observe
            }
            setSearchRecyclerView(results = it)
        }
    }

    private fun setSearchRecyclerView(results: List<DiseaseItem>) {
        val resultsAdapter = DiseaseNameDataAdapter(results, query.capitalize())
        layout.recyclerSearchResults.apply {
            setHasFixedSize(false)
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
        startActivity(this@SearchableActivity.interactivityIntent(diseaseId))
    }

}