package com.example.cdmdda.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.R
import com.example.cdmdda.view.DisplayUtils.attachListeners
import com.example.cdmdda.view.DisplayUtils.generateLinks
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.viewmodel.DisplayCropViewModel
import com.example.cdmdda.viewmodel.factory.DisplayCropViewModelFactory as ViewModelFactory

class DisplayCropActivity : AppCompatActivity() {

    // region // declare: ViewBinding, ViewModel
    private lateinit var viewModel: DisplayCropViewModel
    private lateinit var layout: ActivityDisplayCropBinding
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cropId = intent.getStringExtra("crop_id")!!

        // region // init: ViewModel, ViewBinding
        viewModel = ViewModelProvider(this, ViewModelFactory(application, cropId))
            .get(DisplayCropViewModel::class.java)

        layout = ActivityDisplayCropBinding.inflate(layoutInflater).apply {
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
            layout.apply {
                loadingCrop.hide()
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