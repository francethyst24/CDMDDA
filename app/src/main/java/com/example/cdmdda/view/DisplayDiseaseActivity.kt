package com.example.cdmdda.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.R
import com.example.cdmdda.view.DisplayUtils.attachListeners
import com.example.cdmdda.view.DisplayUtils.generateLinks
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.databinding.ActivityDisplayDiseaseBinding
import com.example.cdmdda.viewmodel.DisplayDiseaseViewModel
import com.example.cdmdda.viewmodel.factory.DisplayDiseaseViewModelFactory as ViewModelFactory

class DisplayDiseaseActivity : AppCompatActivity() {

    // region // declare: ViewModel, ViewBinding
    private lateinit var layout: ActivityDisplayDiseaseBinding
    private lateinit var viewModel: DisplayDiseaseViewModel
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diseaseId = intent.getStringExtra("disease_id")!!

        // region // init: ViewModel, ViewBinding
        viewModel = ViewModelProvider(this, ViewModelFactory(application, diseaseId))
            .get(DisplayDiseaseViewModel::class.java)

        layout = ActivityDisplayDiseaseBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbarDisplayDisease)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                viewModel.disease.observe(this@DisplayDiseaseActivity) { title = it.name }
            }
            setContentView(root)
        }
        // endregion

        // bind: Disease -> UI
        viewModel.disease.observe(this@DisplayDiseaseActivity) {
            layout.apply {
                loadingDisease.hide()
                textDisplayDiseaseName.text = it.name
                textDisplayDiseaseVector.text = it.vector
                val diseasesText = getString(R.string.text_crops) + it.crops.joinToString()
                textCrops.text = diseasesText
                val pairedList = attachListeners(this@DisplayDiseaseActivity, it.crops)
                generateLinks(textCrops,getString(R.string.text_crops).length - 1, *pairedList.toTypedArray())
            }
        }

    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { this@DisplayDiseaseActivity.finish(); true }
        else -> { super.onOptionsItemSelected(item) }
    }

}