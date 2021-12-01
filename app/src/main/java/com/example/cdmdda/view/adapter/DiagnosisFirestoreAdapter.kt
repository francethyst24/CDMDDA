package com.example.cdmdda.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ItemDiagnosisBinding
import com.example.cdmdda.model.dto.Diagnosis
import com.example.cdmdda.view.DisplayUtils.formatDate
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class DiagnosisFirestoreAdapter(options: FirestoreRecyclerOptions<Diagnosis>) :
    FirestoreRecyclerAdapter<Diagnosis, DiagnosisFirestoreAdapter.DiagnosisHolder>(options) {

    private lateinit var listener: DiagnosisEventListener

    inner class DiagnosisHolder(itemBinding: ItemDiagnosisBinding) : ViewHolder(itemBinding.root) {
        private val textDiagnosisName = itemBinding.textDiagnosisName
        private val textDiagnosisDate = itemBinding.textDiagnosisDate
        init {
            itemBinding.root.setOnClickListener{
                val position = adapterPosition
                if (position != NO_POSITION) {
                    listener.onDiagnosisItemClick(snapshots.getSnapshot(position), position)
                }
            }
        }
        fun bind(context: Context, diagnosis: Diagnosis) {
            textDiagnosisName.text = diagnosis.name
            val dateString = "MMM dd yyyy"; val timeString = "HH:mm"
            val start = dateString.indexOfLast { it == " ".single() }
            val toDate = diagnosis.diagnosed_on!!.toDate()
            val todayDate = formatDate(dateString, Date())
            val diagnoseDate  = formatDate(dateString, toDate)
            textDiagnosisDate.text = if (todayDate == diagnoseDate) {
                context.getString(R.string.text_today).plus(formatDate(timeString, toDate))
            } else if (todayDate.substring(start) == diagnoseDate.substring(start)) {
                formatDate("${dateString.substring(0, start)} $timeString", toDate)
            } else {
                formatDate("$dateString $timeString", toDate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisHolder {
        val itemBinding = ItemDiagnosisBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DiagnosisHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DiagnosisHolder, position: Int, model: Diagnosis) {
        holder.bind(holder.itemView.context, model)
    }

    interface DiagnosisEventListener {
        fun onDiagnosisItemClick(documentSnapshot: DocumentSnapshot, position: Int)
    }

    fun setOnItemClickListener(listener: DiagnosisEventListener) { this.listener = listener}


}