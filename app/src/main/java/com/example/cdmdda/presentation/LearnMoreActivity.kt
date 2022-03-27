package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.DiseaseTextUiState
import com.example.cdmdda.data.dto.TextUiState
import com.example.cdmdda.databinding.ActivityLearnMoreBinding
import com.example.cdmdda.presentation.adapter.TextUiStateAdapter
import com.example.cdmdda.presentation.helper.ResourceHelper
import com.example.cdmdda.presentation.utils.interactivity
import com.example.cdmdda.presentation.viewmodel.LearnMoreViewModel

class LearnMoreActivity : AppCompatActivity() {
    private val layout: ActivityLearnMoreBinding by lazy {
        ActivityLearnMoreBinding.inflate(layoutInflater)
    }
    private val viewModel: LearnMoreViewModel by viewModels()
    private val cropStateAdapter: TextUiStateAdapter by lazy {
        TextUiStateAdapter(viewModel.cropUiStates) { onTextUiItemClick(it) }
    }
    private val diseaseStateAdapter: TextUiStateAdapter by lazy {
        TextUiStateAdapter(viewModel.diseaseUiStates) { onTextUiItemClick(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarLearnMore)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        Glide.with(this).load(R.drawable.capitol_overhead).into(layout.imageLearnHeader)

        val resolver = ResourceHelper(applicationContext)

        setCropListView()
        viewModel.cropCount(resolver).observe(this) { position ->
            cropStateAdapter.notifyItemInserted(position)
        }

        setDiseaseListView()
        viewModel.diseaseCount(resolver).observe(this) { position ->
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
        when(item) {
            is CropTextUiState -> interactivity(AppData.CROP, item)
            is DiseaseTextUiState -> interactivity(AppData.DISEASE, item)
        }
    )

}