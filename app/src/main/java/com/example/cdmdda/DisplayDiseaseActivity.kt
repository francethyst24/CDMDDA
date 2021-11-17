package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.viewModelFactories.DisplayCropViewModelFactory
import com.example.cdmdda.viewModelFactories.DisplayDiseaseViewModelFactory
import com.example.cdmdda.viewmodels.DisplayCropViewModel
import com.example.cdmdda.viewmodels.DisplayDiseaseViewModel

class DisplayDiseaseActivity : AppCompatActivity() {

    // declare: ViewBinding, ViewModel
    private lateinit var binding: ActivityDisplayCropBinding
    private lateinit var viewModel: DisplayDiseaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diseaseId = intent.getStringExtra("disease_id")!!

        // region -- init: ViewBinding, ViewModel
        binding = ActivityDisplayCropBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this@DisplayDiseaseActivity,
            DisplayDiseaseViewModelFactory(application, diseaseId)
        ).get(DisplayDiseaseViewModel::class.java)

        val toolbar = binding.toolbarDisplayCrop
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            viewModel.disease.observe(this@DisplayDiseaseActivity) { title = it.name }
        }
        setContentView(binding.root)
        // endregion

        viewModel.disease.observe(this@DisplayDiseaseActivity) {
            binding.apply {
                textDisplayCropName.text = it.name
                textDisplayCropSciName.text = it.vector
                val diseasesText = getString(R.string.text_crops) + it.crops.joinToString()
                textDiseases.text = diseasesText

                val pairedList = DisplayUtils.attachOnClickListeners(this@DisplayDiseaseActivity, it.crops)
                DisplayUtils.generateLinks(textDiseases,getString(R.string.text_crops).length - 1,
                    *pairedList.toTypedArray()
                )
            }
        }
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@DisplayDiseaseActivity.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}