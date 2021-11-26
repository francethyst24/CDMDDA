package com.example.cdmdda

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.DisplayUtils.attachListeners
import com.example.cdmdda.DisplayUtils.generateLinks
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.viewmodels.DisplayCropViewModel
import com.example.cdmdda.viewModelFactories.DisplayCropViewModelFactory as ViewModelFactory

class DisplayCropActivity : AppCompatActivity() {

    // region // declare: ViewBinding, ViewModel
    private lateinit var viewModel: DisplayCropViewModel
    private lateinit var binding: ActivityDisplayCropBinding
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cropId = intent.getStringExtra("crop_id")!!

        // region // init: ViewModel, ViewBinding
        viewModel = ViewModelProvider(this, ViewModelFactory(application, cropId))
            .get(DisplayCropViewModel::class.java)

        binding = ActivityDisplayCropBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbarDisplayCrop)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                viewModel.crop.observe(this@DisplayCropActivity) {
                    title = it.name
                }
            }
            setContentView(root)
        }
        // endregion

        // bind: Crop -> UI
        viewModel.crop.observe(this@DisplayCropActivity) {
            binding.apply {
                textDisplayCropName.text = it.name
                textDisplayCropSciName.text = it.sci_name
                val diseasesText = getString(R.string.text_diseases) + it.diseases.joinToString()
                textDiseases.text = diseasesText
                val pairedList = attachListeners(this@DisplayCropActivity, it.diseases)
                generateLinks(textDiseases, getString(R.string.text_diseases).length - 1, *pairedList.toTypedArray())
            }
        }

    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { this@DisplayCropActivity.finish(); true }
        else -> { super.onOptionsItemSelected(item) }
    }


}