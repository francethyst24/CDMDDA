package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.databinding.ActivityCropProfileBinding
import com.example.cdmdda.presentation.helper.ResourceHelper
import com.example.cdmdda.presentation.utils.attachListeners
import com.example.cdmdda.presentation.utils.generateLinks
import com.example.cdmdda.presentation.viewmodel.CropProfileViewModel
import kotlinx.coroutines.launch

class CropProfileActivity : BaseCompatActivity() {

    // region // declare: ViewBinding, ViewModel
    private val layout: ActivityCropProfileBinding by lazy {
        ActivityCropProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: CropProfileViewModel by viewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CropProfileViewModel(
                    intent.getParcelableExtra("crop_id")!!
                ) as T
            }
        }
    }
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDisplayCrop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = String()
        setContentView(layout.root)

        // bind: CropProfileUiState -> UI
        lifecycleScope.launch {
            viewModel.cropUiState(ResourceHelper(this@CropProfileActivity)).apply {
                Glide.with(this@CropProfileActivity).load(bannerId).into(layout.imageCrop)

                layout.loadingCrop.hide()
                supportActionBar?.title = name
                layout.textCropName.text = name
                layout.textCropSciName.text = sciName
                layout.textCropDesc.text = desc

                val header = "${getString(R.string.ui_text_diseases)}: "
                layout.textDiseases.text = header.plus(diseases.joinToString { it.id })

                val pairs = this@CropProfileActivity.attachListeners(AppData.DISEASE, diseases)
                layout.textDiseases.generateLinks(header.length - 1, *pairs.toTypedArray())

                if (isSupported) {
                    layout.iconCropSupported.visibility = View.VISIBLE
                }

            }

        }

    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@CropProfileActivity.finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}