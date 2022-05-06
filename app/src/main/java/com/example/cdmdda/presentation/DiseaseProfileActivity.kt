package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.common.Constants.CROP
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.StringArray
import com.example.cdmdda.common.utils.AndroidUtils.FLAG_ACTIVITY_CLEAR_TOP
import com.example.cdmdda.common.utils.AndroidUtils.ORIENTATION_X
import com.example.cdmdda.common.utils.AndroidUtils.getStringCompat
import com.example.cdmdda.common.utils.AndroidUtils.intentWith
import com.example.cdmdda.common.utils.AndroidUtils.setDefaults
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.DiseaseUiState
import com.example.cdmdda.data.repository.ImageRepository
import com.example.cdmdda.databinding.ActivityDiseaseProfileBinding
import com.example.cdmdda.domain.model.Disease
import com.example.cdmdda.domain.model.Image
import com.example.cdmdda.presentation.adapter.ImageAdapter
import com.example.cdmdda.presentation.adapter.LinkAdapter
import com.example.cdmdda.presentation.adapter.LinkAdapter.Companion.setLinkAdapter
import com.example.cdmdda.presentation.adapter.StringAdapter
import com.example.cdmdda.presentation.viewmodel.DiseaseProfileViewModel
import com.example.cdmdda.presentation.viewmodel.factory.viewModelBuilder

class DiseaseProfileActivity : BaseCompatActivity() {
    companion object {
        const val TAG = "DiseaseProfileActivity"
    }

    // region // declare: ViewModel, ViewBinding
    private val layout by lazy { ActivityDiseaseProfileBinding.inflate(layoutInflater) }
    private val viewModel by viewModelBuilder { DiseaseProfileViewModel(ImageRepository()) }
    // endregion

    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDisplayDisease)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        val parcel = intent.getParcelableExtra(DISEASE) as DiseaseUiState?
        if (parcel != null) {
            viewModel.diseaseUiState(this, parcel.id).observe(this) { disease ->
                layout.recyclerImages.setImageAdapter(viewModel.imageBitmaps)
                viewModel.imageCount.observe(this) {
                    imageAdapter?.notifyItemInserted(it)
                    layout.recyclerImages.smoothScrollBy(20, 0)
                }
                // bind: Disease -> UI
                bind(disease)
            }
        }
    }

    private fun bind(uiState: Disease) = layout.apply {
        loadingDisease.hide()
        textDiseaseName.text = uiState.id
        supportActionBar?.title = uiState.id
        textDiseaseVector.text = getString(uiState.vector)
        textDiseaseCause.text = getString(uiState.cause)
        if (uiState.isSupported) iconDiseaseSupported.visibility = View.VISIBLE

        val symptoms = resources.getStringArray(uiState.symptoms)
        val treatments = resources.getStringArray(uiState.treatments)
        layout.recyclerSymptoms.setStringAdapter(symptoms)
        layout.recyclerTreatments.setStringAdapter(treatments)

        val headerText = "${getStringCompat(viewModel.uiHeadCrops)}: "

        textCropsAffected.text = headerText.plus(
            uiState.cropsAffected.joinToString { getStringCompat(it.name) }
        )

        val adapter = LinkAdapter(uiState.cropsAffected, headerText.lastIndex, onTextClick = {
            val intent = intentWith(CROP, it as CropText).addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })
        textCropsAffected.setLinkAdapter(adapter)
    }

    private fun RecyclerView.setImageAdapter(images: List<Image>) {
        setDefaults()
        layoutManager = LinearLayoutManager(this@DiseaseProfileActivity, ORIENTATION_X, false)
        imageAdapter = ImageAdapter(images)
        adapter = imageAdapter
    }

    private fun RecyclerView.setStringAdapter(data: StringArray) {
        setDefaults()
        layoutManager = LinearLayoutManager(this@DiseaseProfileActivity)
        adapter = StringAdapter(data)
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