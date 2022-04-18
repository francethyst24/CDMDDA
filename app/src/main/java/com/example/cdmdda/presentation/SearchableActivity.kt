package com.example.cdmdda.presentation

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.ContextUtils.intentWith
import com.example.cdmdda.common.StringUtils.capitalize
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.databinding.ActivitySearchableBinding
import com.example.cdmdda.domain.usecase.GetDiseaseItemUseCase
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase
import com.example.cdmdda.presentation.adapter.ResultsAdapter
import com.example.cdmdda.data.repository.DiseaseDataRepository
import com.example.cdmdda.presentation.viewmodel.SearchableViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class SearchableActivity : BaseCompatActivity() {
    companion object {
        const val TAG = "SearchableActivity"
    }

    private val layout: ActivitySearchableBinding by lazy {
        ActivitySearchableBinding.inflate(layoutInflater)
    }
    private val viewModel: SearchableViewModel by viewModels {
        CreateWithFactory {
            SearchableViewModel(
                SearchDiseaseUseCase(helper.allDiseases, GetDiseaseItemUseCase(helper)),
                query.toString(),
            )
        }
    }

    private val helper: DiseaseDataRepository by lazy { DiseaseDataRepository(this) }
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
            layout.textNoResults.visibility = it?.let {
                // UI Event: RecyclerView
                setSearchRecyclerView(it)
                View.GONE
            } ?: run { View.VISIBLE }
        }
    }

    private fun setSearchRecyclerView(results: List<DiseaseItem>) {
        val resultsAdapter = ResultsAdapter(this, results, query.capitalize(),
            onItemClick = { startActivity(intentWith(extra = DISEASE, it)) }
        )
        layout.recyclerSearchResults.also {
            it.setHasFixedSize(false)
            it.layoutManager = LinearLayoutManager(this)
            layout.loadingSearch.hide()
            it.adapter = resultsAdapter
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