package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.data.dto.TextUiState
import com.example.cdmdda.databinding.ActivityLearnMoreBinding
import com.example.cdmdda.presentation.adapter.TextUiStateAdapter
import com.example.cdmdda.presentation.helper.ResourceHelper
import com.example.cdmdda.presentation.viewmodel.LearnMoreViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class LearnMoreActivity : BaseCompatActivity() {
    private val layout: ActivityLearnMoreBinding by lazy {
        ActivityLearnMoreBinding.inflate(layoutInflater)
    }
    private val viewModel: LearnMoreViewModel by viewModels {
        CreateWithFactory { LearnMoreViewModel(resourceHelper) }
    }
    private val cropStateAdapter: TextUiStateAdapter by lazy {
        TextUiStateAdapter(viewModel.cropUiStates) { onTextUiItemClick(it) }
    }
    private val diseaseStateAdapter: TextUiStateAdapter by lazy {
        TextUiStateAdapter(viewModel.diseaseUiStates) { onTextUiItemClick(it) }
    }
    private val resourceHelper: ResourceHelper by lazy { ResourceHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarLearnMore)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        Glide.with(this).load(R.drawable.capitol_overhead).into(layout.imageLearnHeader)

        setCropListView()
        viewModel.cropCount().observe(this) { position ->
            cropStateAdapter.notifyItemInserted(position)
        }

        setDiseaseListView()
        viewModel.diseaseCount().observe(this) { position ->
            diseaseStateAdapter.notifyItemInserted(position)
        }
    }

    private fun setCropListView() {
        layout.listSupportedCrops.also {
            it.setHasFixedSize(false)
            it.isNestedScrollingEnabled = false
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = cropStateAdapter
        }
    }

    private fun setDiseaseListView() {
        layout.listSupportedDiseases.also {
            it.setHasFixedSize(false)
            it.isNestedScrollingEnabled = false
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = diseaseStateAdapter
        }
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }

    private fun onTextUiItemClick(item: TextUiState) = startActivity(
        when (item) {
            is CropText -> interactivity(AppData.CROP, item)
            is DiseaseText -> interactivity(AppData.DISEASE, item)
        }
    )

}