package com.example.cdmdda.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.view.utils.ListenerUtils.attachListeners
import com.example.cdmdda.view.utils.generateLinks
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

        // bind: Crop -> UI
        viewModel.crop(this).let {
            layout.loadingCrop.hide()
            supportActionBar?.title = it.name
            layout.textCropName.text = it.name
            layout.textCropSciName.text = it.sciName
            layout.textCropDesc.text = it.desc

            val diseasesText = "${getString(R.string.ui_text_diseases)}: "
            layout.textDiseases.text = diseasesText.plus(it.diseases.joinToString())

            val pairs = attachListeners(this@DisplayCropActivity, it.diseases)
                layout.textDiseases.generateLinks(
                getString(R.string.ui_text_diseases).length - 1,
                *pairs.toTypedArray()
            )

            viewModel.cropBanner.observe(this@DisplayCropActivity) { banner ->
                Glide.with(this).load(banner).into(layout.imageCrop)
            }

            viewModel.cropSupported(this@DisplayCropActivity).observe(this@DisplayCropActivity) { isSupported ->
                if (isSupported) {
                    layout.iconCropSupported.visibility = View.VISIBLE
                }
            }
        }

    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { this@DisplayCropActivity.finish(); true }
        else -> { super.onOptionsItemSelected(item) }
    }

}