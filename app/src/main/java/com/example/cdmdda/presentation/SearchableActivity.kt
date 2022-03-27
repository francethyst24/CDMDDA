package com.example.cdmdda.presentation

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.data.SuggestionsProvider
import com.example.cdmdda.databinding.ActivitySearchableBinding
import com.example.cdmdda.domain.usecase.GetDiseaseItemUseCase
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase
import com.example.cdmdda.presentation.adapter.ResultsAdapter
import com.example.cdmdda.presentation.helper.GlobalHelper
import com.example.cdmdda.presentation.helper.ResourceHelper
import com.example.cdmdda.presentation.utils.capitalize
import com.example.cdmdda.presentation.utils.interactivity
import com.example.cdmdda.presentation.viewmodel.SearchableViewModel

class SearchableActivity : BaseCompatActivity() {
    companion object { const val TAG = "SearchableActivity" }
    private val layout: ActivitySearchableBinding by lazy {
        ActivitySearchableBinding.inflate(layoutInflater)
    }
    private val viewModel: SearchableViewModel by viewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchableViewModel(
                    SearchDiseaseUseCase(
                        GlobalHelper(applicationContext),
                        GetDiseaseItemUseCase(ResourceHelper(this@SearchableActivity))
                    ),
                    query.toString(),
                    //GlobalHelper(applicationContext).allDiseases(),
                ) as T
            }
        }
    }

    private var query: String? = null
    private var resultsAdapter : ResultsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout.also {
            setSupportActionBar(it.toolbarSearch)
            setContentView(it.root)
        }
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        intent.also {
            super.onNewIntent(it)
            this.intent = it
            handleIntent(it)
        }
    }

    private fun handleIntent(intent: Intent) {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH != intent.action) {
            this@SearchableActivity.finish()
            return
        }
        query = intent.getStringExtra(SearchManager.QUERY)
        // update UI : query -> Toolbar(Title)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.ui_head_search).plus(": $query")
        }


        resultsAdapter = ResultsAdapter(viewModel.resultList, query.toString().capitalize(), onItemClick = {
            startActivity(interactivity(AppData.DISEASE, it))
        })


        setSearchRecyclerView()

        // Saving Queries
        val auth = SuggestionsProvider.AUTHORITY
        val mode = SuggestionsProvider.MODE
        val provider = SearchRecentSuggestions(this, auth, mode)
        viewModel.resultCount(provider).observe(this) {
            when (it) {
                -1 -> {
                    layout.loadingSearch.hide()
                    layout.textNoResults.visibility = View.VISIBLE
                }
                else -> {
                    layout.loadingSearch.hide()
                    layout.textNoResults.visibility = View.GONE
                    Log.i(TAG, "itemInserted(${it}), resultCount=${viewModel.resultList.size}")
                    resultsAdapter?.notifyItemInserted(it)
                }
            }
        }

    }

    private fun setSearchRecyclerView() {
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