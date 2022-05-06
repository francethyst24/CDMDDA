package com.example.cdmdda.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.cdmdda.common.Constants.CROP
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.utils.AndroidUtils.FLAG_ACTIVITY_CLEAR_TOP
import com.example.cdmdda.common.utils.AndroidUtils.ORIENTATION_LANDSCAPE
import com.example.cdmdda.common.utils.AndroidUtils.intentWith
import com.example.cdmdda.data.dto.CropUiState
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.databinding.ActivityCropProfileBinding
import com.example.cdmdda.domain.model.Crop
import com.example.cdmdda.presentation.adapter.LinkAdapter
import com.example.cdmdda.presentation.adapter.LinkAdapter.Companion.setLinkAdapter
import com.example.cdmdda.presentation.viewmodel.CropProfileViewModel

class CropProfileActivity : BaseCompatActivity() {

    // region // declare: ViewBinding, ViewModel
    private val layout by lazy { ActivityCropProfileBinding.inflate(layoutInflater) }
    private val viewModel: CropProfileViewModel by viewModels()
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDisplayCrop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = String()
        setContentView(layout.root)

        val parcel = intent.getParcelableExtra(CROP) as CropUiState?
        if (parcel != null) {
            viewModel.cropUiState(this, parcel).observe(this) {
                Glide.with(this).load(it.bannerId).into(layout.imageCrop)
                // bind: Crop -> UI
                bind(it)
            }
        }
    }

    private fun bind(uiState: Crop) = with(layout) {
        loadingCrop.hide()
        val name = getString(uiState.name)
        supportActionBar?.title = name
        textCropName.text = name
        textCropSciName.text = getString(uiState.sciName)
        textCropDesc.text = getString(uiState.desc)
        if (uiState.isSupported) iconCropSupported.visibility = View.VISIBLE

        val headerText = "${getString(viewModel.uiHeadDiseases)}: "
        textDiseases.text = headerText.plus(uiState.diseases.joinToString { it.id })

        val adapter = LinkAdapter(uiState.diseases, headerText.lastIndex, onTextClick = {
            val intent = intentWith(DISEASE, it as DiseaseText).addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })
        textDiseases.setLinkAdapter(adapter)
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) = with(newConfig) {
        super.onConfigurationChanged(newConfig)
        layout.aplDisplayCrop.setExpanded(orientation != ORIENTATION_LANDSCAPE)
    }
}