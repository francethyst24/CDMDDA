package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.DisplayUtils.attachListeners
import com.example.cdmdda.DisplayUtils.generateLinks
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.viewmodels.DisplayDiseaseViewModel
import com.example.cdmdda.viewModelFactories.DisplayDiseaseViewModelFactory as ViewModelFactory

class DisplayDiseaseActivity : AppCompatActivity() {

    // region // declare: ViewModel, ViewBinding
    private lateinit var binding: ActivityDisplayCropBinding
    private lateinit var viewModel: DisplayDiseaseViewModel
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diseaseId = intent.getStringExtra("disease_id")!!

        // region // init: ViewModel, ViewBinding
        viewModel = ViewModelProvider(this, ViewModelFactory(application, diseaseId))
            .get(DisplayDiseaseViewModel::class.java)

        binding = ActivityDisplayCropBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbarDisplayCrop)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                viewModel.disease.observe(this@DisplayDiseaseActivity) { title = it.name }
            }
            setContentView(root)
        }
        // endregion

        // bind: Disease -> UI
        viewModel.disease.observe(this@DisplayDiseaseActivity) {
            binding.apply {
                textDisplayCropName.text = it.name
                textDisplayCropSciName.text = it.vector
                val diseasesText = getString(R.string.text_crops) + it.crops.joinToString()
                textDiseases.text = diseasesText
                val pairedList = attachListeners(this@DisplayDiseaseActivity, it.crops)
                generateLinks(textDiseases,getString(R.string.text_crops).length - 1, *pairedList.toTypedArray())
            }
        }

    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { this@DisplayDiseaseActivity.finish(); true }
        else -> { super.onOptionsItemSelected(item) }
    }

}