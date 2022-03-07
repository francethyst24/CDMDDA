package com.example.cdmdda.view.adapter

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ItemDiagnosisBinding
import com.example.cdmdda.model.dto.Diagnosis
import com.example.cdmdda.view.utils.formatDate
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class DiagnosisFirestoreAdapter(options: FirestoreRecyclerOptions<Diagnosis>) : FirestoreRecyclerAdapter<Diagnosis, DiagnosisFirestoreAdapter.DiagnosisHolder>(options) {

    private lateinit var listener: DiagnosisItemEventListener

    inner class DiagnosisHolder(private val itemLayout: ItemDiagnosisBinding) : ViewHolder(itemLayout.root) {
        init {
            itemLayout.root.setOnClickListener{
                val position = bindingAdapterPosition
                if (position != NO_POSITION) {
                    listener.onDiagnosisItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }

        fun bind(context: Context, diagnosis: Diagnosis) {
            itemLayout.textDiagnosisName.text = diagnosis.name
            val dateString = "MMM dd yyyy"; val timeString = "HH:mm"
            val start = dateString.indexOfLast { it == " ".single() }
            val toDate = diagnosis.diagnosed_on.toDate()
            val todayDate = Date().formatDate(dateString)
            val diagnoseDate  = toDate.formatDate(dateString)
            itemLayout.textDiagnosisDate.text = when {
                todayDate == diagnoseDate -> {
                    "${context.getString(R.string.ui_text_today)} ".plus(toDate.formatDate(timeString))
                }
                todayDate.substring(start) == diagnoseDate.substring(start) -> {
                    toDate.formatDate("${dateString.substring(0, start)} $timeString")
                }
                else -> {
                    toDate.formatDate("$dateString $timeString")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisHolder {
        val itemBinding = ItemDiagnosisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiagnosisHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DiagnosisHolder, position: Int, model: Diagnosis) {
        holder.bind(holder.itemView.context, model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        if (itemCount == 0) {
            listener.onEmptyListReturned()
        }
    }

    interface DiagnosisItemEventListener {
        fun onDiagnosisItemClick(documentSnapshot: DocumentSnapshot, position: Int)
        fun onEmptyListReturned()
    }

    fun setOnItemClickListener(_listener: DiagnosisItemEventListener) { listener = _listener}


}