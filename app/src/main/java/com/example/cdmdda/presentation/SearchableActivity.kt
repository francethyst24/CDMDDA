package com.example.cdmdda.presentation

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.common.AndroidUtils.getStringArray
import com.example.cdmdda.common.AndroidUtils.intentWith
import com.example.cdmdda.common.AndroidUtils.setDefaults
import com.example.cdmdda.common.Constants.ALL_DISEASES
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.StringUtils.capitalize
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.databinding.ActivitySearchableBinding
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult
import com.example.cdmdda.presentation.adapter.SearchQueryAdapter
import com.example.cdmdda.presentation.viewmodel.SearchableViewModel
import com.example.cdmdda.presentation.viewmodel.factory.viewModelBuilder

class SearchableActivity : BaseCompatActivity() {
    companion object {
        const val TAG = "SearchableActivity"
    }

    private val layout by lazy { ActivitySearchableBinding.inflate(layoutInflater) }
    private val viewModel by viewModelBuilder {
        SearchableViewModel(
            SearchDiseaseUseCase(getStringArray(ALL_DISEASES)),
            query.toString(),
        )
    }

    private var query: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarSearch)
        setContentView(layout.root)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH != intent.action) {
            finish()
            return
        }

        query = intent.getStringExtra(SearchManager.QUERY)
        // update UI : query -> Toolbar(Title)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(viewModel.uiHeadSearch).plus(": $query")
        }

        // UI Event: Saving Queries
        viewModel.resultCount(this).observe(this) {
            layout.loadingSearch.hide()
            layout.textNoResults.visibility = if (it is SearchResult.Success) {
                // UI Event: RecyclerView
                setSearchRecyclerView(it.list)
                View.GONE
            } else { View.VISIBLE }
        }
    }

    private fun setSearchRecyclerView(results: List<DiseaseItem>) {
        val searchQueryAdapter = SearchQueryAdapter(
            results, query.capitalize()
        ) { startActivity(intentWith(DISEASE, it)) }
        layout.loadingSearch.hide()
        with(layout.recyclerSearchResults) {
            setDefaults()
            layoutManager = LinearLayoutManager(this@SearchableActivity)
            adapter = searchQueryAdapter
        }
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }

}