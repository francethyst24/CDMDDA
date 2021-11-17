package com.example.cdmdda

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.viewModelFactories.DisplayCropViewModelFactory
import com.example.cdmdda.viewmodels.DisplayCropViewModel

class DisplayCropActivity : AppCompatActivity() {

    // declare: ViewBinding, ViewModel
    private lateinit var binding: ActivityDisplayCropBinding
    private lateinit var viewModel: DisplayCropViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cropId = intent.getStringExtra("crop_id")!!

        // region -- init: ViewBinding, ViewModel
        binding = ActivityDisplayCropBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this@DisplayCropActivity,
            DisplayCropViewModelFactory(application, cropId)
        ).get(DisplayCropViewModel::class.java)

        val toolbar = binding.toolbarDisplayCrop
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            viewModel.crop.observe(this@DisplayCropActivity) { title = it.name }
        }
        setContentView(binding.root)
        // endregion

        viewModel.crop.observe(this@DisplayCropActivity) {
            binding.apply {
                textDisplayCropName.text = it.name
                textDisplayCropSciName.text = it.sci_name
                val diseasesText = getString(R.string.text_diseases) + it.diseases.joinToString()
                textDiseases.text = diseasesText

                val pairedList = DisplayUtils.attachOnClickListeners(this@DisplayCropActivity, it.diseases)
                DisplayUtils.generateLinks(textDiseases,getString(R.string.text_diseases).length - 1,
                    *pairedList.toTypedArray()
                )
            }
        }


    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@DisplayCropActivity.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


}