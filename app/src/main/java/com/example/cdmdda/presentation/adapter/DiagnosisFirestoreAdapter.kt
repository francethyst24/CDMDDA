package com.example.cdmdda.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.R
import com.example.cdmdda.common.DateTimeFormat
import com.example.cdmdda.common.DateTimeFormat.formatDate
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.example.cdmdda.databinding.ItemDiagnosisBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import com.example.cdmdda.common.DateTimeFormat.DATE_FORMAT as dateFormat
import com.example.cdmdda.common.DateTimeFormat.TIME_FORMAT as timeFormat

class DiagnosisFirestoreAdapter(
    options: FirestoreRecyclerOptions<DiseaseDiagnosis>,
    private val defaultDispatcher: CoroutineDispatcher,
    private val onItemClicked: (DiseaseDiagnosis) -> Unit,
    private val onPopulateList: (Boolean) -> Unit,
) : FirestoreRecyclerAdapter<DiseaseDiagnosis, DiagnosisFirestoreAdapter.DiagnosisFirestoreHolder>(options) {
    private val today: String = Date().formatDate(DateTimeFormat.DATE_FORMAT)

    inner class DiagnosisFirestoreHolder(private val itemLayout: ItemDiagnosisBinding) : ViewHolder(itemLayout.root) {

        fun bind(diagnosisUiState: DiseaseDiagnosis) = itemLayout.apply {
            textDiagnosisName.text = diagnosisUiState.id
            root.setOnClickListener {
                onItemClicked(diagnosisUiState)
            }

            MainScope().launch {
                val rawDate = withContext(defaultDispatcher) {
                    diagnosisUiState.diagnosedOn.toDate()
                }
                val diagnosisTime = withContext(defaultDispatcher) {
                    rawDate.formatDate(timeFormat)
                }
                val diagnosisDate = withContext(defaultDispatcher) {
                    rawDate.formatDate(dateFormat)
                }
                val separator = withContext(defaultDispatcher) {
                    dateFormat.indexOfLast { it == " ".single() }
                }
                val diagnosisDay = withContext(defaultDispatcher) {
                    rawDate.formatDate(dateFormat.substring(0, separator))
                }

                textDiagnosisDate.text = when {
                    today == diagnosisDate -> {
                        itemView.context.getString(R.string.ui_text_today)
                            .plus(" $diagnosisTime")
                    }
                    today.substring(separator) == diagnosisDate.substring(separator) -> {
                        diagnosisDay.plus(" $diagnosisTime")
                    }
                    else -> diagnosisDate.plus(" $diagnosisTime")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisFirestoreHolder {
        val itemLayout = ItemDiagnosisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiagnosisFirestoreHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: DiagnosisFirestoreHolder, position: Int, model: DiseaseDiagnosis) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        onPopulateList(itemCount == 0)
    }

}