package com.example.cdmdda.presentation

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.data.dto.ImageResource
import com.example.cdmdda.data.repository.OnlineImageRepository
import com.example.cdmdda.databinding.ActivityDiseaseProfileBinding
import com.example.cdmdda.presentation.adapter.ImageAdapter
import com.example.cdmdda.presentation.adapter.StringAdapter
import com.example.cdmdda.presentation.helper.GlobalHelper
import com.example.cdmdda.presentation.helper.ResourceHelper
import com.example.cdmdda.presentation.utils.attachListeners
import com.example.cdmdda.presentation.utils.generateLinks
import com.example.cdmdda.presentation.viewmodel.DiseaseProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiseaseProfileActivity : BaseCompatActivity() {
    companion object { const val TAG = "DiseaseProfileActivity" }

    // region // declare: ViewModel, ViewBinding
    private val layout: ActivityDiseaseProfileBinding by lazy {
        ActivityDiseaseProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: DiseaseProfileViewModel by viewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DiseaseProfileViewModel(
                    intent.getParcelableExtra("disease_id")!!,
                    OnlineImageRepository(set, Dispatchers.IO)
                ) as T
            }
        }
    }
    // endregion
    private val set : String by lazy {
        GlobalHelper(applicationContext).dataset()
    }
    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDisplayDisease)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        viewModel.fetchDiseaseImages(this)
        setImageRecycler(viewModel.imageBitmaps)
        viewModel.imageCount.observe(this@DiseaseProfileActivity) {
            Log.i(TAG, "itemInserted($it), imageCount=${viewModel.imageBitmaps.size}")
            imageAdapter?.notifyItemInserted(it)
        }

        // bind: DiseaseProfileUiState -> UI
        lifecycleScope.launch {
            viewModel.diseaseUiState(ResourceHelper(this@DiseaseProfileActivity)).apply {
                layout.apply {
                    loadingDisease.hide()
                    textDiseaseName.text = id
                    supportActionBar?.title = id
                    textDiseaseVector.text = vector
                    textDiseaseCause.text = cause
                    setSymptomRecycler(symptoms)
                    setTreatmentRecycler(treatments)

                    val header = "${getString(R.string.ui_text_crops)}: "
                    textCropsAffected.text = header.plus(cropsAffected.joinToString { it.name })

                    val pairs = this@DiseaseProfileActivity.attachListeners(AppData.CROP, cropsAffected)
                    textCropsAffected.generateLinks(header.length - 1, *pairs.toTypedArray())

                    if (isSupported) {
                        iconDiseaseSupported.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setImageRecycler(diseaseImages: List<ImageResource>) {
        imageAdapter = ImageAdapter(diseaseImages)
        layout.recyclerImages.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DiseaseProfileActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    private fun setSymptomRecycler(symptoms: List<String>) {
        layout.recyclerSymptoms.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DiseaseProfileActivity)
            adapter = StringAdapter(symptoms)
        }
    }

    private fun setTreatmentRecycler(treatments: List<String>) {
        layout.recyclerTreatments.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DiseaseProfileActivity)
            adapter = StringAdapter(treatments)
        }
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@DiseaseProfileActivity.finish(); true
        }
        else ->
            super.onOptionsItemSelected(item)
    }

}