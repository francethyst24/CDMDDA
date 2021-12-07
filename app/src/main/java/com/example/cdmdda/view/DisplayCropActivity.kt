package com.example.cdmdda.view

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.view.utils.StringUtils.attachListeners
import com.example.cdmdda.view.utils.TextViewUtils
import com.example.cdmdda.viewmodel.DisplayCropViewModel
import com.example.cdmdda.viewmodel.factory.DisplayCropViewModelFactory as ViewModelFactory

class DisplayCropActivity : BaseCompatActivity() {

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
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = String()
            setContentView(root)
        }
        // endregion
        viewModel.cropBanner.observe(this@DisplayCropActivity) { banner ->
            layout.imageCrop.setImageDrawable(banner)
        }
        viewModel.cropIcon.observe(this@DisplayCropActivity) { icon ->
            layout.imageCropIcon.setImageDrawable(icon)
        }

        // bind: Crop -> UI
        viewModel.crop.observe(this@DisplayCropActivity) { crop ->
            layout.apply {
                loadingCrop.hide()
                val cropNameTl = crop.getName(this@DisplayCropActivity)
                supportActionBar?.title = cropNameTl
                textCropName.text = cropNameTl
                textCropSciName.text = crop.sci_name
                textCropDesc.text = crop.desc
                val diseasesText = getString(R.string.text_diseases) + crop.diseases.joinToString()
                textDiseases.text = diseasesText
                val pairs = attachListeners(this@DisplayCropActivity, crop.diseases)
                TextViewUtils.generateLinks(textDiseases, getString(R.string.text_diseases).length - 1, *pairs.toTypedArray())
            }
        }
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { this@DisplayCropActivity.finish(); true }
        else -> { super.onOptionsItemSelected(item) }
    }

}