package com.example.cdmdda.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.R
import com.example.cdmdda.common.BoolCallback
import com.example.cdmdda.common.Callback
import com.example.cdmdda.common.Constants.WHITESPACE
import com.example.cdmdda.common.DateTimeUtils.DATE_FORMAT
import com.example.cdmdda.common.DateTimeUtils.TIME_FORMAT
import com.example.cdmdda.common.DateTimeUtils.toStringWith
import com.example.cdmdda.common.Diagnosis
import com.example.cdmdda.common.ParcelCallback
import com.example.cdmdda.data.dto.DiagnosisUiState
import com.example.cdmdda.databinding.ItemDiagnosisBinding
import com.example.cdmdda.databinding.ItemDiagnosisBinding.inflate
import com.example.cdmdda.presentation.adapter.DiagnosisFirestoreAdapter.DiagnosisHolder
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import java.util.*


class DiagnosisFirestoreAdapter constructor(
    private val lifecycleOwner: LifecycleOwner,
    options: FirestoreRecyclerOptions<Diagnosis>,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val onItemClicked: ParcelCallback,
    private val onPopulateList: BoolCallback,
    private val onChildAdded: Callback,
) : FirestoreRecyclerAdapter<Diagnosis, DiagnosisHolder>(options) {

    inner class DiagnosisHolder(private val itemLayout: ItemDiagnosisBinding) : ViewHolder(itemLayout.root) {

        fun bind(diagnosis: Diagnosis): Unit = itemLayout.run {
            root.setOnClickListener { onItemClicked(diagnosis) }
            lifecycleOwner.lifecycleScope.launch {
                val uiState = diagnosis.toUiState(itemView.context)
                textDiagnosisName.text = uiState.id
                textDiagnosisDate.text = uiState.diagnoseDateString
            }
        }

        private suspend fun Diagnosis.toUiState(context: Context) = withContext(defaultDispatcher) {
            DiagnosisUiState(
                id,
                diagnosedOn.toDate().let {
                    val nowAsync = async { Date().toStringWith(DATE_FORMAT) }
                    val dDateAsync = async { it.toStringWith(DATE_FORMAT) }
                    val dTimeAsync = async { it.toStringWith(TIME_FORMAT) }
                    val todayAsync = async { context.getString(R.string.ui_text_today) }
                    val spl = async { DATE_FORMAT.indexOf(WHITESPACE) }

                    val now = nowAsync.await()
                    val dDate = dDateAsync.await()
                    val dTime = dTimeAsync.await()
                    val nowNoYearAsync = async { now.substring(spl.await()) }
                    val rawNoYearAsync = async { dDate.substring(0, spl.await()) }
                    val dDateNoYearAsync = async { dDate.substring(spl.await()) }
                    if (now == dDate) "${todayAsync.await()} $dTime"
                    else {
                        if (nowNoYearAsync.await() == dDateNoYearAsync.await()) {
                            "${rawNoYearAsync.await()} $dTime"
                        } else "$dDate $dTime"
                    }
                }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisHolder {
        val itemLayout = inflate(LayoutInflater.from(parent.context), parent, false)
        return DiagnosisHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: DiagnosisHolder, position: Int, model: Diagnosis) = holder.bind(model)

    override fun onDataChanged() {
        super.onDataChanged()
        onPopulateList(itemCount == 0)
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
        if (type == ChangeEventType.ADDED) {
            notifyItemInserted(0)
            onChildAdded()
        }
    }

}