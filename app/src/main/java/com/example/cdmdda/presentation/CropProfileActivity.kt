package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cdmdda.common.Constants.CROP
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.Constants.FLAG_ACTIVITY_CLEAR_TOP
import com.example.cdmdda.common.ContextUtils.intentWith
import com.example.cdmdda.data.dto.Crop
import com.example.cdmdda.data.dto.CropProfile
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.databinding.ActivityCropProfileBinding
import com.example.cdmdda.domain.usecase.GetCropProfileUseCase
import com.example.cdmdda.presentation.adapter.TextViewLinksAdapter
import com.example.cdmdda.presentation.adapter.TextViewLinksAdapter.Companion.setAdapter
import com.example.cdmdda.data.repository.CropDataRepository
import com.example.cdmdda.presentation.viewmodel.CropProfileViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class CropProfileActivity : BaseCompatActivity() {

    // region // declare: ViewBinding, ViewModel
    private val layout: ActivityCropProfileBinding by lazy {
        ActivityCropProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: CropProfileViewModel by viewModels {
        CreateWithFactory {
            CropProfileViewModel(GetCropProfileUseCase(cropResource))
        }
    }
    private val cropResource: CropDataRepository by lazy { CropDataRepository(this) }
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDisplayCrop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = String()
        setContentView(layout.root)

        val parcel = intent.getParcelableExtra(CROP) as Crop?
        lifecycleScope.launchWhenStarted {
            parcel?.let {
                viewModel.cropUiState(it).observe(this@CropProfileActivity) { crop ->
                    // bind: CropProfile -> UI
                    bind(crop)
                }
            }
        }
    }

    private fun bind(uiState: CropProfile) = layout.apply {
        Glide.with(this@CropProfileActivity).load(uiState.bannerId).into(layout.imageCrop)

        loadingCrop.hide()
        getString(uiState.name).let {
            supportActionBar?.title = it
            textCropName.text = it
        }
        textCropSciName.text = getString(uiState.sciName)
        textCropDesc.text = getString(uiState.desc)
        if (uiState.isSupported) iconCropSupported.visibility = View.VISIBLE

        val header = "${getString(viewModel.uiHeadDiseases)}: "
        textDiseases.text = header.plus(uiState.diseases.joinToString { it.id })

        val adapter = TextViewLinksAdapter(uiState.diseases, header.length - 1) {
            val parcel = it as DiseaseText
            startActivity(intentWith(extra = DISEASE, parcel).addFlags(FLAG_ACTIVITY_CLEAR_TOP))
        }
        textDiseases.setAdapter(adapter)
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}