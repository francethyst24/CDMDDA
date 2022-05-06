package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cdmdda.R
import com.example.cdmdda.common.Constants.CROP
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.utils.AndroidUtils.intentWith
import com.example.cdmdda.common.utils.AndroidUtils.setDefaults
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.data.dto.UiState
import com.example.cdmdda.databinding.ActivityLearnMoreBinding
import com.example.cdmdda.presentation.adapter.UiStateAdapter
import com.example.cdmdda.presentation.viewmodel.LearnMoreViewModel

class LearnMoreActivity : BaseCompatActivity() {
    private val layout by lazy { ActivityLearnMoreBinding.inflate(layoutInflater) }
    private val viewModel: LearnMoreViewModel by viewModels()
    private val cropStateAdapter by lazy {
        UiStateAdapter(viewModel.cropUiStates) {
            onTextUiItemClick(it)
        }
    }
    private val diseaseStateAdapter by lazy {
        UiStateAdapter(viewModel.diseaseUiStates) {
            onTextUiItemClick(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarLearnMore)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        Glide.with(this).load(R.drawable.capitol_overhead).into(layout.imageLearnHeader)

        layout.listSupportedCrops.setUiStateAdapter(cropStateAdapter)
        viewModel.cropCount(this).observe(this) { position ->
            cropStateAdapter.notifyItemInserted(position)
        }

        layout.listSupportedDiseases.setUiStateAdapter(diseaseStateAdapter)
        viewModel.diseaseCount(this).observe(this) { position ->
            diseaseStateAdapter.notifyItemInserted(position)
        }
    }

    private fun RecyclerView.setUiStateAdapter(adapter: UiStateAdapter) {
        setDefaults()
        this.layoutManager = LinearLayoutManager(this@LearnMoreActivity)
        this.adapter = adapter
    }


    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }

    private fun onTextUiItemClick(item: UiState) = startActivity(
        when (item) {
            is CropText -> intentWith(CROP, item)
            is DiseaseText -> intentWith(DISEASE, item)
        }
    )

}