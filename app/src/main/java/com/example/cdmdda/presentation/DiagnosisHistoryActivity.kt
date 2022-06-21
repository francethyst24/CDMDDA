package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.common.Constants
import com.example.cdmdda.common.DiagnosisUiState
import com.example.cdmdda.common.utils.AndroidUtils.ORIENTATION_Y
import com.example.cdmdda.common.utils.AndroidUtils.intentWith
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.example.cdmdda.data.repository.ImageRepository
import com.example.cdmdda.databinding.ActivityDiagnosisHistoryBinding
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.presentation.adapter.DiagnosisAdapter
import com.example.cdmdda.presentation.fragment.ShowDiagnosisDialog
import com.example.cdmdda.presentation.fragment.ShowDiagnosisDialog.OnGotoDiseaseListener
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.viewmodel.DiagnosisHistoryViewModel
import com.example.cdmdda.presentation.viewmodel.ShowDiagnosisDialogViewModel
import com.example.cdmdda.presentation.viewmodel.factory.viewModelBuilder
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DiagnosisHistoryActivity : AppCompatActivity(), OnGotoDiseaseListener {
    private val layout by lazy { ActivityDiagnosisHistoryBinding.inflate(layoutInflater) }
    private val model by viewModelBuilder {
        DiagnosisHistoryViewModel(
            LocaleHelper.getLocale(this@DiagnosisHistoryActivity)
                ?: "en",
            GetDiagnosisHistoryUseCase(),
        )
    }
    private val showDiagnosisDialogModel by viewModelBuilder {
        ShowDiagnosisDialogViewModel(ImageRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDiagnosisHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = model.title

        setContentView(layout.root)

        lifecycleScope.launchWhenStarted {
            model.getDiagnosisOptions()?.let { setupRecyclerView(it) }
        }
        observeDiagnosisUiState()
    }

    private var diagnosisAdapter: DiagnosisAdapter? = null

    private fun setupRecyclerView(options: FirestoreRecyclerOptions<DiagnosisUiState>) {
        diagnosisAdapter = DiagnosisAdapter(
            lifecycleOwner = this,
            options = options,
            orientation = ORIENTATION_Y,
            onItemClicked = {
                if (it !is DiseaseDiagnosis) return@DiagnosisAdapter
                showDiagnosisDialogModel.persistClickedDiagnosis(it)
                ShowDiagnosisDialog().show(supportFragmentManager, ShowDiagnosisDialog.TAG)
                //startActivity(intentWith(Constants.DISEASE, it))
            },
            onPopulateList = { isEmpty ->
                model.finishedLoadingDiagnosis()
                model.finishedReturnedDiagnosis(isEmpty)
            },
            onChildAdded = {},
        )
        diagnosisAdapter?.startListening()
        with(layout.recyclerDiagnosisHistory) {
            setHasFixedSize(true)
            with(this@DiagnosisHistoryActivity) {
                layoutManager = LinearLayoutManager(this, ORIENTATION_Y, false)
                val divider = DividerItemDecoration(this, ORIENTATION_Y)
                getDrawable(model.uiDrawDividerY)?.let { divider.setDrawable(it) }
                addItemDecoration(divider)
            }
            adapter = diagnosisAdapter
        }
    }

    // UI Event: Memory Leak
    override fun onDestroy() {
        diagnosisAdapter?.stopListening()
        super.onDestroy()
    }

    private fun observeDiagnosisUiState() {
        model.loadingDiagnosisUiState.observe(this) {
            if (!it) layout.loadingDiagnosis.visibility = View.GONE
        }
        model.isEmptyDiagnosisUiState.observe(this) {
            layout.textNoDiagnosis.visibility = if (it) {
                diagnosisAdapter?.notifyDataSetChanged()
                View.VISIBLE
            } else View.GONE
        }
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onGotoDiseaseClick(diseaseId: DiseaseDiagnosis) {
        startActivity(intentWith(Constants.DISEASE, diseaseId))
    }
}