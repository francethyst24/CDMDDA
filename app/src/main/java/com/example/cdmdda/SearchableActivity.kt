package com.example.cdmdda

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.adapters.SearchAdapter
import com.example.cdmdda.adapters.SearchResultsAdapter
import com.example.cdmdda.databinding.ActivitySearchableBinding
import com.example.cdmdda.viewmodels.SearchableViewModel
import java.util.*
import com.example.cdmdda.viewModelFactories.SearchableViewModelFactory as ViewModelFactory

class SearchableActivity : AppCompatActivity(), SearchResultsAdapter.OnItemClickListener {

    private lateinit var binding: ActivitySearchableBinding
    private lateinit var viewModel: SearchableViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Intent.ACTION_SEARCH != intent.action) return

        binding = ActivitySearchableBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbarSearch)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_search_header)
                .plus(intent.getStringExtra(SearchManager.QUERY))
        }
        setContentView(binding.root)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH != intent.action) return

        val query = intent.getStringExtra(SearchManager.QUERY).toString()
            .split(Regex("\\s+")).joinToString("\u2000") {
                it.replaceFirstChar { firstChar ->
                    if (firstChar.isLowerCase()) {
                        firstChar.titlecase(Locale.getDefault())
                    } else firstChar.toString()
                }
            }
        viewModel = ViewModelProvider(this, ViewModelFactory(application, query))
            .get(SearchableViewModel::class.java)


        viewModel.diseaseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataList = mutableListOf<String>()
                for (document in task.result!!) {
                    dataList.add(document.id)
                }
                setSearchRecyclerView(dataList, query)
            }
        }
    }

    private fun setSearchRecyclerView(dataList: MutableList<String>, query: String) {
        dataList.sortBy { it }

        val resultList = mutableListOf<String>()
        dataList.forEach { if (it.startsWith(query)) resultList.add(it) }
        dataList.removeAll(resultList)

        dataList.forEach {
            it.split(Regex("\\s+")).forEach { word ->
                if (word.startsWith(query)) {
                    resultList.add(it)
                }
            }
        }

        val resultsAdapter = SearchResultsAdapter(resultList, query)
        binding.rcvSearchResults.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@SearchableActivity)
            binding.pbSearch.hide()
            adapter = resultsAdapter
        }
        resultsAdapter.setOnItemClickListener(this@SearchableActivity)
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@SearchableActivity.finish()
            true
        }
        else -> { super.onContextItemSelected(item) }
    }

    override fun onResultItemClick(diseaseId: String) {
        val startDiseaseActivity = Intent(this@SearchableActivity, DisplayDiseaseActivity::class.java)
        startDiseaseActivity.putExtra("disease_id", diseaseId)
        startActivity(startDiseaseActivity)
    }
}