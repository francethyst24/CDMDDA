package com.example.cdmdda.presentation

import android.content.Intent
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
import com.example.cdmdda.data.dto.CropProfileUiState
import com.example.cdmdda.data.dto.CropUiState
import com.example.cdmdda.data.dto.DiseaseTextUiState
import com.example.cdmdda.databinding.ActivityCropProfileBinding
import com.example.cdmdda.domain.usecase.GetCropProfileUseCase
import com.example.cdmdda.presentation.adapter.setAdapter
import com.example.cdmdda.presentation.helper.CropResourceHelper
import com.example.cdmdda.presentation.adapter.TextViewLinksAdapter
import com.example.cdmdda.presentation.viewmodel.CropProfileViewModel

class CropProfileActivity : BaseCompatActivity() {

    // region // declare: ViewBinding, ViewModel
    private val layout: ActivityCropProfileBinding by lazy {
        ActivityCropProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: CropProfileViewModel by viewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CropProfileViewModel(GetCropProfileUseCase(cropResource)) as T
            }
        }
    }
    private val cropResource: CropResourceHelper by lazy { CropResourceHelper(this) }
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDisplayCrop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = String()
        setContentView(layout.root)

        val parcel = intent.getParcelableExtra("crop_id") as CropUiState?
        lifecycleScope.launchWhenStarted {
            parcel?.let {
                viewModel.cropUiState(it).observe(this@CropProfileActivity) { crop ->
                    // bind: CropProfileUiState -> UI
                    crop.bind()
                }
            }
        }
    }

    private fun CropProfileUiState.bind() = layout.apply {
        Glide.with(this@CropProfileActivity).load(bannerId).into(layout.imageCrop)

        loadingCrop.hide()
        supportActionBar?.title = name
        textCropName.text = name
        textCropSciName.text = sciName
        textCropDesc.text = desc
        if (isSupported) iconCropSupported.visibility = View.VISIBLE

        val header = "${getString(R.string.ui_text_diseases)}: "
        textDiseases.text = header.plus(diseases.joinToString { it.id })

        val adapter = TextViewLinksAdapter(diseases, header.length-1) {
            val parcel = it as DiseaseTextUiState
            val flag = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(interactivity(AppData.DISEASE, parcel).addFlags(flag))
        }
        textDiseases.setAdapter(adapter)
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